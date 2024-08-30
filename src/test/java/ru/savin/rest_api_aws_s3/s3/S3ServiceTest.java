package ru.savin.rest_api_aws_s3.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class S3ServiceTest {

	@Autowired
	private S3Service s3Service;

	@Test
	public void testCreateBucket() {
		s3Service.createBucket();
	}

	@Test
	public void testListBuckets(){
		s3Service.listBuckets();
	}

	@Test
	public void testUpload(){
		s3Service.uploadFile();
	}

	@Test
	public void tesGetList(){
		s3Service.listFiles();
	}
}