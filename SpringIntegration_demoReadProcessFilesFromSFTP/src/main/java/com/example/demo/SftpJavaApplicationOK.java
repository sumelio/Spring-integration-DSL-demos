package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.batch.runtime.JobExecution;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.dsl.SftpOutboundGatewaySpec;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@SpringBootApplication
@IntegrationComponentScan
public class SftpJavaApplicationOK {

    private static final String PATH_REMOTE_DIRECTORY = "/home/freddy.lemus/testFTP/";
	private static final String PATH_LOCAL_DIRECTORY = "/home/freddylemus/Documents/Lending/TrackingMarketing/LOCAL_SFTP/done/";
	
	private static final long FIXED_DELAY_SECONDS = 5;
	private static final long MIN_AGE_FILE_PROCESS_SECONDS = 10;
	private static final String CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE = "sftpInboundAdapter";
	private static final String REGEX_FILTER_FILE = ".*\\.csv$";

	public static void main(String[] args) {
        new SpringApplicationBuilder(SftpJavaApplicationOK.class)
            .web(false)
            .run(args);
    }
    
    @Bean
    public SessionFactory<LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("172.18.10.11");
        factory.setPort(422);
        factory.setUser("freddy.lemus");
        factory.setPassword("Diciembre.2017");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<LsEntry>(factory);
    }


    @Bean
    public IntegrationFlow sftpInboundFlow() {
        return IntegrationFlows
            .from(Sftp.inboundAdapter(this.sftpSessionFactory())
                    .preserveTimestamp(true)                    
                    .remoteDirectory(PATH_REMOTE_DIRECTORY)
                    .regexFilter(REGEX_FILTER_FILE)
                    .filter( f -> filterByAge(f) )
                    .localFilenameExpression("#this.toString() + ''")
                    .localDirectory(new File(PATH_LOCAL_DIRECTORY)),
                         e -> e.id(CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE)                         
                    .autoStartup(true)                    
                    .poller(Pollers.fixedDelay(FIXED_DELAY_SECONDS * 1000))  )
                    .enrichHeaders(                    		
                    		h -> h
                            .headerExpression(FileHeaders.REMOTE_DIRECTORY, "'" + PATH_REMOTE_DIRECTORY + "' + payload.getName() ")
                            .headerExpression(FileHeaders.RENAME_TO, "'" + PATH_REMOTE_DIRECTORY +"done/' + payload.getName() " )
                    		)                    
                    .filter(isSuccesful() )
                    .<File, Boolean>route(p 
                    		-> validate(p) , 
                    		mapping -> mapping.subFlowMapping(true, (sf)   -> sf.channel("success")
                    				                                          .filter(isSuccesDelete())
                    				                                          .handle(ftpOutboundGatewayMoveFile("SUCCESS"))
                                                              )
                    		                  .subFlowMapping(false, (sf) -> sf.channel("fail")
                    		                		                           .filter(isSuccesDelete())
                    		                		                           .handle(ftpOutboundGatewayMoveFile("ERROR")))
                                                         
                    		)
                    //.filter(isSuccesDelete())
                    //.handle(ftpOutboundGateway(),  e -> e.advice(retryAdvice()) )
                    //.handle(ftpOutboundGateway() )
                    .handle(m -> System.out.println("File " + m.getPayload()) )
            .get();
      }
    
	private Boolean validate(Object p) {
		// TODO Auto-generated method stub
		return true;
	}

	private List<LsEntry> filterByAge(LsEntry[] f) {		
		return Stream.of(f)
		.filter(e -> ! e.getFilename().equals(".") && ! e.getFilename().equals("..") )
		.filter((entryFile) -> {
			long lastModifiedSeconds = (entryFile.getAttrs()).getMTime();
			long nowSeconds = (Instant.now().toEpochMilli() / 1000);
			long fileAge = (nowSeconds - lastModifiedSeconds);
			boolean isAdd =  fileAge >= MIN_AGE_FILE_PROCESS_SECONDS;
			System.out.println( " entry [" + entryFile.getFilename() + "]" + ", fileAge=["+fileAge+"], filter ["+isAdd+"]" );
			return isAdd;
   		  }
		).collect(Collectors.toList());
	}
	

	public SftpOutboundGatewaySpec ftpOutboundGatewayMoveFile(String result) {
		System.out.println("Moving files...... ");
	    return Sftp
	            .outboundGateway(sftpSessionFactory(),
	            		AbstractRemoteFileOutboundGateway.Command.MV, "headers['file_remoteDirectory']"). 
	            renameExpression("headers['file_renameTo'].replaceFirst('csv', 'csv___"+result+"__" +  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss.SSS")) + ".processed')");
	}
	
	
	
	/*
	@Bean
	public Advice retryAdvice() {
		RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();

		return advice;
	}
	
	
    @Bean
    public QueueChannelSpec remoteFileOutputChannel() {
        return MessageChannels.queue();
    }

   */
        
    public MessageSelector isSuccesful() {
    	
    	return new MessageSelector() {
			
			@Override
			public boolean accept(Message<?> message) {
				System.out.println("Process files :::::::::::::::::::::::::::" + message.getPayload());
				
				if(message.getPayload() instanceof File) {
					File file = (File)message.getPayload();

					try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {

						 stream.forEach(line -> { System.out.println("============= " + line); } );

					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}					
				}
				return true;
			}
		};
    }
    
    public MessageSelector isSuccesDelete() {
    	
    	return new MessageSelector() {
			
			@Override
			public boolean accept(Message<?> message) {
				System.out.println("Moving file :::::::::::::::::::::::::::" + message.getPayload());
				
				if(message.getPayload() instanceof File) {
					File file = (File)message.getPayload();
					String path = file.getAbsolutePath().replace("/done/" +file.getName() , "/") +"success/";
					System.out.println(" path = " + path);
					System.out.println(" === > " + file.renameTo(new File(path + file.getName())));
					return true;
				}
				return true;
			}
		};
    }



	

}