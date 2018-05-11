package com.example.demo;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

import org.aopalliance.aop.Advice;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageProcessorSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.integration.file.FileHeaders;

import com.jcraft.jsch.ChannelSftp.LsEntry;

public class Test {

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
	public IntegrationFlow ftpInboundFlow() {
		return IntegrationFlows.from(Sftp.inboundAdapter(sftpSessionFactory()).preserveTimestamp(true)
				// .deleteRemoteFiles(true)
				.remoteDirectory("/home/freddy.lemus/testFTP/")
				.regexFilter(".*\\.csv$")
				.localFilenameExpression("#this.toString() + ''")
				.localDirectory(new File("/home/freddylemus/Documents/Lending/TrackingMarketing/LOCAL_SFTP/done/")),
				e -> e.id("sftpInboundAdapter")
						.poller(Pollers.fixedRate(2000, TimeUnit.MINUTES).maxMessagesPerPoll(-1)))
				.enrichHeaders(h -> h
						// headers necessary for moving remote files (ftpOutboundGateway)
						.headerExpression(FileHeaders.RENAME_TO, "'/home/blabla/done/' + payload.getName()")
						.headerExpression(FileHeaders.REMOTE_FILE, "payload.getName()")
						.header(FileHeaders.REMOTE_DIRECTORY, "/home/blabla/")
						// headers necessary for moving local files
						// (fileOutboundGateway_MoveToProcessedDirectory)
						.headerExpression(FileHeaders.ORIGINAL_FILE, "payload.getAbsolutePath()")
						.headerExpression(FileHeaders.FILENAME, "payload.getName()"))
				.transform(fileToJobLaunchRequestTransformer())
				//.handle(jobLaunchingGw(), e -> e.advice(retryAdvice()))
				.<JobExecution, Boolean>route(p -> BatchStatus.COMPLETED.equals(p.getBatchStatus()),
						mapping -> mapping.subFlowMapping(true, (sf) ->

						sf.handle(org.springframework.batch.core.JobExecution.class,
								(p, h) -> jobExecutionToString(p, (String) h.get(FileHeaders.REMOTE_DIRECTORY),
										(String) h.get(FileHeaders.REMOTE_FILE)))
								.handle(ftpOutboundGateway())
								.handle(Boolean.class,
										(p, h) -> BooleanToString(p, (String) h.get(FileHeaders.FILENAME)))
								.handle(fileOutboundGateway_MoveToProcessedDirectory())

						)

						.subFlowMapping(false, sf -> sf.channel("nullChannel")

						))

				.handle(logger()).get();
	}

	@Bean
	@ServiceActivator(inputChannel = "myLogChannel")
	public MessageHandler logger() {
		LoggingHandler loggingHandler = new LoggingHandler(LoggingHandler.Level.INFO.name());
		loggingHandler.setLoggerName("Test");
		return loggingHandler;
	}

	private String BooleanToString(Boolean p, String string) {
		// TODO Auto-generated method stub
		return Boolean.toString(p);
	}

	private Object jobExecutionToString(org.springframework.batch.core.JobExecution p, String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

	private MessageHandler fileOutboundGateway_MoveToProcessedDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public Advice retryAdvice() {
		RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();

		return advice;
	}

	private MessageProcessorSpec<?> jobLaunchingGw() {
		// TODO Auto-generated method stub
		return null;
	}

	private String fileToJobLaunchRequestTransformer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedRate(500).get();
	}

	@Bean
	public MessageHandler ftpOutboundGateway() {
		return Sftp.outboundGateway(sftpSessionFactory(), AbstractRemoteFileOutboundGateway.Command.MV, "payload")
				.renameExpression("headers['file_renameTo']").get();
	}

}
