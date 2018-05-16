package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.dsl.SftpOutboundGatewaySpec;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;


import com.jcraft.jsch.ChannelSftp.LsEntry;

@SpringBootApplication
//@IntegrationComponentScan
public class SftpJavaApplicationOK {

    private static final String PATH_REMOTE_DIRECTORY = "/home/freddy.lemus/fileMarketing/upload/";
    private static final String PATH_REMOTE_RENAME_TO_DIRECTORY = "/home/freddy.lemus/fileMarketing/processed/";
    
	private static final String PATH_LOCAL_DIRECTORY = "./processing/";
	
	private static final long FIXED_DELAY_SECONDS = 10;
	private static final long MIN_AGE_FILE_PROCESS_SECONDS = 20;
	private static final String CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE = "sftpInboundAdapter";
	private static final String REGEX_FILTER_FILE_CSV = ".*\\.csv$";

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

    interface ShortToByteFunction {
    	 
        byte applyAsByte(short s);
     
    }

    
    public enum Result { SUCCESS, FAIL }
    

    @Bean
    public IntegrationFlow sftpInboundFlow() {
        return IntegrationFlows
            .from(Sftp.inboundAdapter(this.sftpSessionFactory())
                    .preserveTimestamp(true)                    
                    .remoteDirectory(PATH_REMOTE_DIRECTORY)
                    .regexFilter(REGEX_FILTER_FILE_CSV)                    
                    .filterFunction( isFileAgeValid.andThen(isProcessUnlocked) )
                    .localDirectory(new File(PATH_LOCAL_DIRECTORY)),
                         e -> e.id(CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE)                         
                    .autoStartup(true)                    
                    .poller(Pollers.fixedRate(FIXED_DELAY_SECONDS, TimeUnit.SECONDS))  )
                    .enrichHeaders(                    		
                    		h -> h
                            .headerExpression(FileHeaders.REMOTE_DIRECTORY, "'" + PATH_REMOTE_DIRECTORY + "' + payload.getName() ")
                            .headerExpression(FileHeaders.RENAME_TO, "'" + PATH_REMOTE_RENAME_TO_DIRECTORY +"' + payload.getName() " )
                    		)
                    .<File, Result>route(file -> readAndProcessFile(file) , 
                    		mapping -> mapping.subFlowMapping(Result.SUCCESS, (sf) -> sf.channel(CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE + "_SUCCESS")
                    				                                                  .filter(isSuccesDelete())
                    				                                                  .handle(ftpOutboundGatewayMoveFile("SUCCESS"))
                                                              )
                    		                  .subFlowMapping(Result.FAIL,    (sf) -> sf.channel(CHANNEL_SFTP_IN_BOUND_ADAPTER_FILE + "_FAIL")
                    		                		                                  .filter(isSuccesDelete())
                    		                		                                  .handle(ftpOutboundGatewayMoveFile("ERROR")))
                    		)
                    .handle(file ->  processFinally(file) )
            .get();
      }
    
    
	public  Function<Optional<LsEntry>, Boolean> isProcessUnlocked = r -> {
		System.out.println("ArrowFuntion isUnlock");		
		if(r.isPresent()) {
			return true;
		}else {
		  return false;	
		}
		
	};
	
	public Function<LsEntry, Optional<LsEntry>> isFileAgeValid = r -> {
		System.out.println("ArrowFuntion isFilAgeValid ");
		if (isFileAgeValid(r))
			return Optional.of(r);
		else
			return Optional.empty();
	};
	

	private Object processFinally(Message<?> f) {
		System.out.println("Finally process for file " + f.getPayload());
		return f;
	}

	private Result readAndProcessFile(File reportCSV) {

		try (Stream<String> stream = Files.lines(Paths.get(reportCSV.getAbsolutePath()))) {
			
			Stream<Report> report = stream.filter(r -> r.length() > 0)
					               .skip(1) // Header
					               .map(lineToReport)
					               .map(mappingLoanProcess);
			
			report.forEach( 
					
					
					r ->  {System.out.println("===========> " + r.getEmail()  + " " + r.getDate());
					
					          if(r.getResult().equals(Result.FAIL) ) {
					        	  throw new RuntimeException();
					          }
					} );

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Result.FAIL;
		}

		return Result.SUCCESS;
	}

	
	
	public Function<String, Report> lineToReport = r -> {
		System.out.println("ArrowFuntion isFilAgeValid ");

		Report report = new Report();

		DefaultLineMapper<Report> lineMapper = new DefaultLineMapper<Report>();
		DelimitedLineTokenizer t = new DelimitedLineTokenizer();
		// t.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
		t.setNames("CUENTA", "CAMPA�A", "ENVIO", "NEWSLETTER", "ASUNTO", "TIPO_PERFIL", "PERFIL", "EMAIL", "FECHA",
				"GEOLOCALIZAZION", "DISPOSITIVO", "NAVEGADOR", "ACCION", "URL", "REBOTE", "VIRAL", "SHARE",
				"DESUSCRIPCION", "FECHA_ENVIO");
		lineMapper.setLineTokenizer(t);

		lineMapper.setFieldSetMapper(new PlayerFieldSetMapper());

		try {
			report = lineMapper.mapLine(r,0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("r " + r);
			report.setResult(Result.FAIL);
			throw new RuntimeException();

		}

		report.setResult(Result.SUCCESS);
		return report;
	};

	
	
	public Function<Report, Report> mappingLoanProcess = r -> {
		System.out.println("ArrowFuntion isFilAgeValid ");

		// Find email in can_borrow_status
		// Set process_loan_id
		// return.
		return r;
	};
	
	private  boolean isFileAgeValid(LsEntry entryFile) {

		long lastModifiedSeconds = (entryFile.getAttrs()).getMTime();
		long nowSeconds = (Instant.now().toEpochMilli() / 1000);
		long fileAge = (nowSeconds - lastModifiedSeconds);
		boolean isAdd = fileAge >= MIN_AGE_FILE_PROCESS_SECONDS;
		System.out.println(" entry [" + entryFile.getFilename() + "]" + ", fileAge=[" + fileAge
				+ "], filter [" + isAdd + "]");
		return isAdd;
	}

	public SftpOutboundGatewaySpec ftpOutboundGatewayMoveFile(String result) {
		System.out.println("Moving files...... ");
	    return Sftp
	            .outboundGateway(sftpSessionFactory(),
	            		AbstractRemoteFileOutboundGateway.Command.MV, "headers['file_remoteDirectory']"). 
	            renameExpression("headers['file_renameTo'].replaceFirst('csv', 'csv___"+result+"__" +  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss.SSS")) + ".processed')");
	}
	

    
    public MessageSelector isSuccesDelete() {
    	
    	return new MessageSelector() {
			
			@Override
			public boolean accept(Message<?> message) {
				System.out.println("Moving file :::::::::::::::::::::::::::" + message.getPayload());
				
				if(message.getPayload() instanceof File) {
					File file = (File)message.getPayload();
					String path = file.getAbsolutePath().replace("/processing/" +file.getName() , "/") +"success/";
					System.out.println(" path = " + path);
					//System.out.println(" === > " + file.renameTo(new File(path + file.getName())));
					file.delete();
					return true;
				} else {
					return false;	
				}
				
			}
		};
    }


    

    protected static class PlayerFieldSetMapper implements FieldSetMapper<Report> {
        public Report mapFieldSet(FieldSet fieldSet) {
        	Report report = new Report();
        	report.setAccount( fieldSet.readString(0));
        	report.setCampaign( fieldSet.readString(1));
        	
        	report.setEmail( fieldSet.readString("EMAIL"));
        	report.setAction( fieldSet.readString("ACCION"));
        	report.setDate( fieldSet.readDate("FECHA", new Date()));

            return report;
        }
    }

	

}