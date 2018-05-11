package com.example.demo;

import java.io.File;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.messaging.Message;



@SpringBootApplication
@IntegrationComponentScan
public class FtpJavaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
            new SpringApplicationBuilder(FtpJavaApplication.class)
                .web(false)
                .run(args);
       // MyGateway gateway = context.getBean(MyGateway.class);
     //   gateway.sendToFtp(new File("/home/freddylemus/Documents/Lending/TrackingMarketing/ICOMMKT_LOG_retorno_Ejemplo.csv"));
        
        
         //gateway = context.getBean(MyGateway.class);
        // gateway.getToFtp(new File("/home/freddylemus/Documents/Lending/TrackingMarketing/ICOMMKT_LOG_retorno_Ejemplo_Temp.csv"));
    }

//    @Bean
//    public SessionFactory<FTPFile> ftpSessionFactory() {
//        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
//        sf.setHost("localhost");
//        sf.setPort(21);
//        sf.setUsername("ftp1user");
//        sf.setPassword("1234");
//        return new CachingSessionFactory<FTPFile>(sf);
//    }

//    @Bean
//    public IntegrationFlow ftpOutboundFlow() {
//        return IntegrationFlows.from("toFtpChannelSend")
//                .handle(Ftp.outboundAdapter(ftpSessionFactory(), FileExistsMode.APPEND)
//                        .useTemporaryFileName(false)
//                        .fileNameExpression("headers['" + FileHeaders.FILENAME + "']")
//                        .remoteDirectory("/home/ftp1user/testFTP/")
//                ).get();
//    }
//
//    @Bean
//    public IntegrationFlow ftpInboundFlow() {
//        return IntegrationFlows.from("toFtpChannelGet")
//                .handle(Ftp.inboundAdapter(ftpSessionFactory())
//                        //.useTemporaryFileName(false)
//                        
//                        //.fileNameExpression("headers['" + FileHeaders.FILENAME + "']")
//                		//.patternFilter(pattern)
//                		.patternFilter("*.csv")
//                		
//                ).get();
//    }

    
    /*
    @Bean
    public IntegrationFlow ftpInboundFlow() {
        return IntegrationFlows
            .from(Ftp.inboundAdapter(this.ftpSessionFactory())
                    .preserveTimestamp(true)
                    .remoteDirectory("/home/ftp1user/testFTP/")
                    .regexFilter(".*\\.csv$")
                    .localFilename(f -> f.toUpperCase() + ".a")
                    .deleteRemoteFiles(false)
                    .localDirectory(new File("/home/freddylemus/Documents/Lending/TrackingMarketing/example")), e -> e.id("ftpInboundAdapter")
                    .autoStartup(true)
                    .poller(Pollers.fixedDelay(15000))
                    
            		)
            .handle(m -> Process(m) )
            
            .get();
    }
    
    */
    @MessagingGateway
    public interface MyGateway {

//         @Gateway(requestChannel = "toFtpChannelSend")
//         void sendToFtp(File file);

         @Gateway(requestChannel= "toFtpChannelGet")
         void getToFtp(File file);

    }
    

    private void Process(Message<?> m) {
    	File fileTemp = ((File)m.getPayload());  
    	
    	
    	System.out.println(" m.getPayload() " + ((File)m.getPayload()).getAbsolutePath() );
    }

}