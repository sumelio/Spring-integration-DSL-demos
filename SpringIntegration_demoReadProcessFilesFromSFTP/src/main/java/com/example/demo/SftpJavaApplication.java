package com.example.demo;

import java.io.InputStream;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.file.filters.AcceptAllFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.sftp.inbound.SftpStreamingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.transformer.StreamTransformer;
import org.springframework.messaging.MessageHandler;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@SpringBootApplication
public class SftpJavaApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SftpJavaApplication.class)
            .web(false)
            .run(args);
    }

//    @Bean
//    public SessionFactory<LsEntry> sftpSessionFactory() {
//        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
//        factory.setHost("172.18.10.11");
//        factory.setPort(422);
//        factory.setUser("freddy.lemus");
//        factory.setPassword("Diciembre.2017");
//        factory.setAllowUnknownKeys(true);
//        return new CachingSessionFactory<LsEntry>(factory);
//    }
//    
//    @Bean
//    @InboundChannelAdapter(channel = "stream" , poller = @Poller(fixedDelay = "5000"))
//    public org.springframework.integration.core.MessageSource<InputStream> ftpMessageSource() {
//        SftpStreamingMessageSource messageSource = new SftpStreamingMessageSource(template());
//        messageSource.setRemoteDirectory("/home/freddy.lemus/testFTP/");
//        messageSource.setFilter(new AcceptAllFileListFilter());
//        messageSource.setMaxFetchSize(1);
//        return messageSource;
//    }
//
//    @Bean
//    @Transformer(inputChannel = "stream", outputChannel = "data")
//    public org.springframework.integration.transformer.Transformer transformer() {
//        return new StreamTransformer("UTF-8");
//    }
//
//    @Bean
//    public SftpRemoteFileTemplate template() {
//        return new SftpRemoteFileTemplate(sftpSessionFactory());
//    }
//
//    @ServiceActivator(inputChannel = "data", adviceChain = "after")
//    @Bean
//    public MessageHandler handle() {
//        return System.out::println;
//    }

//    @Bean
//    public ExpressionEvaluatingRequestHandlerAdvice after() {
//        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
//        advice.setOnSuccessExpressionString(
//                "@template.remove(headers['file_remoteDirectory'] + headers['file_remoteFile'])");
//        advice.setPropagateEvaluationFailures(true);
//        return advice;
//    }

}