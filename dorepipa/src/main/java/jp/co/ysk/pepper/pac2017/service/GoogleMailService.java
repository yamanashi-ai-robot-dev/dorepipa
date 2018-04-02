package jp.co.ysk.pepper.pac2017.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import jp.co.ysk.pepper.pac2017.dao.TMailSendDao;

/**
 * GMailAPIを操作するサービスクラス. Created by ko-aoki on 2016/06/07.
 */
@Service
public class GoogleMailService {

    /** ロガー */
    private final Logger logger = LoggerFactory.getLogger(GoogleMailService.class);

    /**
     * アプリケーション名 Be sure to specify the name of your application. If the
     * application name is {@code null} or blank, the application will log a
     * warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    @Value("${dorepipa.application.name}")
    protected String APPLICATION_NAME;

    /** 資格情報 */
    protected GoogleCredential credential = null;

    /** P12ファイル納箇所. */
    @Value("${dorepipa.p12file.path}")
    protected String P12_PATH;

    /** サービスアカウントID */
    @Value("${dorepipa.service.account.id}")
    protected String SERVICE_ACCOUNT_ID;

    /** サービスアカウントユーザ */
    @Value("${dorepipa.service.account.user}")
    protected String SERVICE_ACCOUNT_USER;

    /** メール送信者名義アカウントユーザ */
    @Value("${dorepipa.mail.sender.account.user}")
    protected String MAIL_SENDER_ACCOUNT_USER;

    /** サービスアカウントユーザの資格情報 */
    protected GoogleCredential CREDENTIAL_SERVICE_ACCOUNT_USER = null;

    @Autowired
    private TMailSendDao tMailSendDao;

    /**
     * 資格情報を生成します.
     *
     * @param serviceAccountUser
     *            サービスアカウントユーザ
     * @param scopes
     *            資格情報のスコープ
     * @return 資格情報
     */
    public GoogleCredential create(String serviceAccountUser, Collection<String> scopes) {

        HttpTransport TRANSPORT = new NetHttpTransport();
        JsonFactory JSON_FACTORY = new JacksonFactory();
        try {
            return new GoogleCredential.Builder().setTransport(TRANSPORT).setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(SERVICE_ACCOUNT_ID).setServiceAccountPrivateKeyFromP12File(new File(P12_PATH))
                    .setServiceAccountScopes(scopes).setServiceAccountUser(serviceAccountUser).build();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * サービスアカウントユーザの資格情報を設定します.
     *
     * @return 資格情報
     */
    protected void authorizeServiceAccountUser() {

        if (CREDENTIAL_SERVICE_ACCOUNT_USER != null) {
            return;
        }
        this.CREDENTIAL_SERVICE_ACCOUNT_USER = this.create(SERVICE_ACCOUNT_USER,
                Arrays.asList(GmailScopes.GMAIL_COMPOSE, DirectoryScopes.ADMIN_DIRECTORY_USER));
    }

    /**
     * GMailクライアントクラスを作成します.
     *
     * @param credential
     *            資格情報
     * @return
     */
    private Gmail createClient(Credential credential) {

        HttpTransport TRANSPORT = new NetHttpTransport();
        JsonFactory JSON_FACTORY = new JacksonFactory();

        return new Gmail.Builder(TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to
     *            送信先リスト
     * @param subject
     *            表題
     * @param bodyText
     *            本文
     * @return MimeMessage.
     * @throws MessagingException
     */
    public MimeMessage createEmail(List<String> toList, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        try {
            email.setFrom(new InternetAddress(this.MAIL_SENDER_ACCOUNT_USER, "どれピパ", "iso-2022-jp"));
            for (String to : toList) {
                email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            }
            email.setSubject(subject);
            // email.setText(bodyText);
            email.setContent(bodyText, "text/html; charset=iso-2022-jp");
        } catch (Exception e) {
            logger.error("メール作成処理でエラー.", e);
            throw new RuntimeException(e);
        }
        return email;
    }

    /**
     * Create a Message from an email
     *
     * @param email
     *            MimeMessage
     * @return base64urlエンコードしたMimeMessage.
     * @throws IOException
     * @throws MessagingException
     */
    public Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * メールを送信します。送信元は代表のサービスアカウントです.
     *
     * @param toList
     *            送信先リスト
     * @param subject
     *            表題
     * @param bodyText
     *            本文
     */
    public void sendMail(String subject, String bodyText) {

        this.authorizeServiceAccountUser();
        Gmail client = this.createClient(this.CREDENTIAL_SERVICE_ACCOUNT_USER);

        try {
            MimeMessage message = this.createEmail(tMailSendDao.selectAll(), subject, bodyText);
            client.users().messages().send(this.SERVICE_ACCOUNT_USER, this.createMessageWithEmail(message)).execute();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
