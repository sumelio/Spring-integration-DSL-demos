package com.example.demo;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication
	public class FtpJavaApplicationMV {

//	    public static void main(String[] args) {
//	        new SpringApplicationBuilder(FtpJavaApplication.class)
//	            .web(false)
//	            .run(args);
//	    }
//
//	    @Bean
//	    public SessionFactory<FTPFile> ftpSessionFactory() {
//	        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
//	        sf.setHost("localhost");
//	        sf.setPort(21);
//	        sf.setUsername("ftp1user");
//	        sf.setPassword("1234");
//	        return new CachingSessionFactory<FTPFile>(sf);
//	    }
//
//	    @Bean
//	    @ServiceActivator(inputChannel = "ftpChannel")
//	    public MessageHandler handler() {
//	        FtpOutboundGateway ftpOutboundGateway =
//	                          new FtpOutboundGateway(ftpSessionFactory(), "mv", "'/home/freddy.lemus/testFTP/'");
//	        ftpOutboundGateway.setOutputChannelName("lsReplyChannel");
//	        return ftpOutboundGateway;
//	    }

	}