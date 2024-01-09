# aws background processing for S3 log error report  (To be implemented)

This architectural design is a batch process designed to extract error messages from the application log files stored in an S3 bucket and subsequently send EMails to the production support team. It utilizes the project [S3 log search](https://github.com/github4daniel/s3logSearch) as search engine. The <B> <I>S3 log search</B></I> is web application doing free text search for the gz files stored in S3 bucket.
  

### 1 Business Requirements

FluntD collects application logs, archives them, and periodically transfers the archived file to an S3 bucket when the buffer is full. A background process is required to handle the log information, categorize and generate statistics, and then send Emails to the production support team

### 2 Design

The defining characteristics of the application is its on-demand nature, eliminating the necessity for continuous 24/7 operation like traditional service or server. To best align with this requirement, the optimal solution is to leverage AWS Serverless architect for the application. The following architecture diagram is what I propose. 

 <li>Upon FluentD placing a log file into the S3 bucket, the S3 system generate a ObjectPutEvent. The event serves as a trigger for the Lambda function. The Lambda function (S3 Search Engine in diagram) is responsible for extracting pertinent log information from file and subsequently saving into the database.  This Lambda function is similar to S3 log search being implemented.
 </li></br>
 <li>
 AWS EventBridge acts as a time scheduler, trigger a AWS Step Function. The entry Lambda function collects event context, such as the type of error, time lines for log entries and then conduct the search the error message in gz log files.
</li></br>
<li>
The first branch of Lambda is dedicated to aggregating and processing error data for email content generation, and utilize AWS Simple Email Service (SES) to send email to recipients.
The second branch of  Lambda is responsible for aggregating and processing error data as well, but it focus on generating data for the Dashboard.
</li></br>

![Step Function](img/awsstepfunction.png)

*Figure 1: Architecture Diagram batch Job for S3 log error extraction

### 3 Selection of AWS services

Team has proposed other AWS services for evaluation:

<p><b>AWS Glue + Athena: </b>
AWS Glue serves as an ETL (Extract, Transform, Load) tool, dedicated to the preparation and transformation of data. It automatically discovers, catalogs, and transforms data from various sources. AWS Glue use Crawlers for discovering metadata from diverse data sources. Athena functions as a query tool, enabling the selection of subsets of the prepared and transformed data stored in Amazon S3. Together, AWS Glue and Athena contribute to a comprehensive data processing and querying workflow.

<p><b>AWS Step Function: </b>
AWS step function is a fully managed AWS web service that enables you to coordinate and orchestrate multiple service into Serverless workflow. It supports parallel processing.

<p><b>AWS Batch: </b>
AWS batch is a fully managed AWS web service that allow you to run batching computing workloads on the AWS cloud.  You can provision EC2 and docker or Serverless Fargate (without provision EC2 instance). The AWS batch is suitable for long running process.  

#### 3.1 Why Choose AWS full managed serverless services in this project?
In classifying applications, web applications adopt a request/response style and must operate on a 24/7 schedule. All non-web based applications fit into the category of batch jobs (or background process).  Batch Job applications plays a more prominent role in corporate strategies as they are pivotal in transforming raw data into business insights through various processes such as data analytics, reporting and machine learning etc.  The batch application is task based and on-demand in nature and not running on 24/7 schedule. Leveraging AWS serverless services allow us to fully meeting the benefit of efficiency and cost with no maintenance cost 

#### 3.2 Cost of AWS Service

<p><b>Compute Resources (CPU and Memory):</b> AWS provides a variety of compute services, including EC2 (Elastic Compute Cloud) instances, where customers incur charges based on the type and size of the chosen virtual machine (VM). In essence, AWS charges users based on the resources they consume. This explains why the provisioning of AWS services is typically free as executing the provisioning script demands minimal computing resources to update AWS resources in your account. Yet the costs only begin to accrue when you initiate active usage or running of the provisioned resource.

<p><b>Storage Space:</b> AWS offers various storage options, including Amazon S3 (Simple Storage Service) for object storage and Amazon EBS (Elastic Block Store) for block storage or Amazon EFS (Elastic File System).

<p><b>Data Transfer:</b> Costs may be incurred for data transfer between AWS services, regions, or the internet. Ingress (incoming) data transfer is usually free, but egress (outgoing) data transfer has associated costs.

<p><b> Additional Services:</b> AWS provides a wide range of services beyond basic compute and storage, such as databases, machine learning, analytics, and more. The usage of these additional services contributes to the overall cost.

<p><b>Reserved Instances and Savings Plans:</b> AWS offers options like Reserved Instances and Savings Plans, which allow customers to commit to a specific usage in advance in exchange for discounted pricing compared to on-demand rates.

#### 3.3 Basic Plan vs Premium Features and DIY
In AWS, some services, like AWS S3 and EC2, function as essential components similar to basic plans in a cell phone service. These services are often fundamental and unavoidable, For example if managing your own server fleet is a requirement, then you oft EC2. However, many other services offered by AWS can be considered as premium features. For instance, AWS Glue provides a comprehensive ETL solution, including data cataloging, filtering, and transformation. None AWS ETL solution have been in existence for a considerable time, for example, open source Apache Atlas provides Data Catalog solution, and Apache Sparks provide ETL solution, etc, and opts for services like AWS Glue may incur higher costs and potentially bind your application more tightly to the AWS ecosystem. In such cases, organizations may weigh the need to build their applications into containerized environments, and running on AWS services like Fargate, to maintain flexibility and possibly optimize costs.

#### 3.4 Customization vs Generalization
Another crucial aspect to consider when utilizing AWS services is the balance between customization and generalization. AWS services are designed to cater to a broad range of use cases, providing general solutions. However, if your business requirements demand a higher degree of customization, it might be prudent to explore building a custom solution. Custom solutions can be tailored precisely to meet specific business needs, offering a more finely tuned and specialized approach.

#### 3.5 Summary
When applications need to run as services or servers, AWS offers solutions like EKS (Elastic Kubernetes Service) or ECS (Elastic Container Service). For task-oriented applications, AWS provides serverless options such as Lambda, Step Functions, and Fargate.    


### 4. AWS Service Provision
Use Terraform as AWS provision tool.
