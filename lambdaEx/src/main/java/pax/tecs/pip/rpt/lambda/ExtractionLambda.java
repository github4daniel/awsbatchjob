package pax.tecs.pip.rpt.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

import pax.tecs.pip.rpt.AppConfig;
import pax.tecs.pip.rpt.service.ExtractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExtractionLambda implements RequestHandler<S3Event, String> {

	private static final Logger logger = LoggerFactory.getLogger(ExtractionLambda.class);

	@Override
	public String handleRequest(S3Event s3event, Context context) {
		try {
			S3EventNotificationRecord rec = s3event.getRecords().get(0);
			String srcBucket = rec.getS3().getBucket().getName();
			String srcKey = rec.getS3().getObject().getKey();
			logger.info("ExtractionLambda is triggered to process the file {} in the bucket {}" , srcKey, srcBucket);
			try (AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(
					AppConfig.class)) {
				ExtractService extractService = appContext.getBean(ExtractService.class);
				extractService.processLog(srcBucket, srcKey);
			}
			return "SUCCESS";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}