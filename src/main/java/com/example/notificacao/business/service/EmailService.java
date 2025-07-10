package com.example.notificacao.business.service;

import com.example.notificacao.business.dto.TarefasDTO;
import com.example.notificacao.infrastructure.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    public String nomeRemetente;


    public void enviaEmail(TarefasDTO dto){

        try{

            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

            //Setando o Remetente
            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));

            //Setando o Destinatario
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario()));

            //Setando o Assunto do email
            mimeMessageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();

            //Declarando as variaveis do Thymeleaf e setando de acordo com as informacoes do dto
            context.setVariable("nomeTarefa", dto.getNomeTarefa());
            context.setVariable("dataEvento", dto.getDataEvento());
            context.setVariable("descricao", dto.getDescricao());

            //Seta o template como texto do email
            String template = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(template, true);

            //Envia o email
            javaMailSender.send(mensagem);



        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o email" + e.getCause());
        }
    }

}
