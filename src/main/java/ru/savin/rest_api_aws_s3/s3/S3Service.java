package ru.savin.rest_api_aws_s3.s3;





import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;


import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
	private final AmazonS3 s3client;


	public void createBucket() {
		String bucketName = "s3testslay18";
		if (s3client.doesBucketExistV2(bucketName)) {
			log.info("Bucket {} уже существует, используйте другое имя", bucketName);
			return;
		}
		s3client.createBucket(bucketName);
	}

	public void listBuckets(){
		List<Bucket> buckets = s3client.listBuckets();
		log.info("buckets: {}", buckets);
	}

	@SneakyThrows
	public void uploadFile() {
		String bucketName = "s3testrest";
		ClassLoader loader = S3Service.class.getClassLoader();
		File file = new File(loader.getResource("test.file").getFile());
		s3client.putObject(bucketName,	"test.file", file);
	}

	public void listFiles() {
		String bucketName = "slaytest";

		ObjectListing objects = s3client.listObjects(bucketName);
		for(S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
			log.info("File name: {}", objectSummary.getKey());
		}
	}
}