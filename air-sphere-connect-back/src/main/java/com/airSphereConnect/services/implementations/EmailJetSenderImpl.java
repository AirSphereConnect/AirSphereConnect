package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.AlertsDto;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailJetSenderImpl {

    private static final Logger logger = LoggerFactory.getLogger(EmailJetSenderImpl.class);

    private final MailjetClient mailjetClient;

    public EmailJetSenderImpl(MailjetClient mailjetClient) {
        this.mailjetClient = mailjetClient;
    }

    /**
     * Envoie un email via Mailjet.
     *
     * @param to      Email destinataire
     * @param subject Sujet de l'email
     * @param text    Corps texte/HTML simplifié (ici on fait un HTML minimal)
     */
    public void sendEmail(String to, String username, String subject, AlertsDto dto) {
        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", to) // Remplacer par votre adresse expéditeur
                                            .put("Name", username))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", to)
                                                    .put("Name", "")))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, "<p>" + dto + "</p>")
                                    .put(Emailv31.Message.TEXTPART, dto)
                            ));

            MailjetResponse response = mailjetClient.post(request);

            if (response.getStatus() == 200) {
                logger.info("Email envoyé avec succès à {}", to);
            } else {
                logger.error("Erreur envoi email : status {} - {}", response.getStatus(), response.getData());
            }
        } catch (Exception e) {
            logger.error("Exception lors de l'envoi de l'email : ", e);
        }
    }
}


