package pax.tecs.pip.rpt.service;

import org.springframework.stereotype.Component;

import pax.tecs.pip.rpt.dao.ExtractDao;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class ExtractService {

	private S3Client s3Client;
	private ExtractDao extractDao;

	public ExtractService(S3Client s3Client, ExtractDao extractDao) {
		this.s3Client=s3Client;
		this.extractDao = extractDao;	
	}

	public void processLog(String srcBucket, String srcKey) {
		// TODO Auto-generated method stub
		
	}

}
