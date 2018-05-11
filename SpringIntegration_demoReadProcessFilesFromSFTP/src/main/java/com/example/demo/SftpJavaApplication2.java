package com.example.demo;

import java.io.File;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@SpringBootApplication
public class SftpJavaApplication2 {

//    public static void main(String[] args) {
//        new SpringApplicationBuilder(SftpJavaApplication.class)
//                .web(false)
//                .run(args);
//    }
//
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
//    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
//        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
//        fileSynchronizer.setDeleteRemoteFiles(false);
//        fileSynchronizer.setRemoteDirectory("/home/freddy.lemus/testFTP");
//        fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.csv"));
//
//        return fileSynchronizer;
//    }
//
//    @Bean
//    @InboundChannelAdapter(channel = "sftpChannel", poller =  @Poller(fixedDelay = "6000", maxMessagesPerPoll = "1") )
//    public org.springframework.integration.core.MessageSource<File> sftpMessageSource() {
//        SftpInboundFileSynchronizingMessageSource source =
//                new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
//        source.setLocalDirectory(new File("/home/freddylemus/Documents/shell/local/"));
//        source.setAutoCreateLocalDirectory(true);
//        source.setLocalFilter(new AcceptOnceFileListFilter<File>());
//        //source.setMaxFetchSize(1);
//        return source;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "sftpChannel")
//    public MessageHandler handler() {
//        return new MessageHandler() {
//
//
//            @Override
//            public void handleMessage(org.springframework.messaging.Message<?> message) throws MessagingException {
//                // TODO Auto-generated method stub
//
//            }
//
//        };
//    }

}