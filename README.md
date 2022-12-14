
## How To Run

1. Clone Project from Github
```
 git clone https://github.com/Nazel7/banking-application-mini.git

```
2. Navigate to the project directory and follow the step below

### Build project
```
./mvnw clean install -DskipTests
```
### Run the Test cases
```
./mvnw test

```
### Run on Docker

1. build the image
```
docker build -t bnk-dk-02:lastest .
```
2. instantiate image container on port 4000
```
docker run -p 4000:4000 docker.io/library/bnk-dk-02:lastest
```

## URL to APis collections:
```
bank_base_url_d: http://localhost:4000/apis/decagon-bank/

postman_collection: https://www.getpostman.com/collections/d19c6a94c0166fcf0d29
```

## Run RabbitMQ for the notification capability on transactions in the application
```
$ docker run -d -p 15672:15672 -p 5672:5672 rabbitmq:3-management-alpine
```

# Writing
1. My Experience with core banking
```
I have extensive experience working in the Fintech companies and building Digigal banking solutions working in the bank.
I have worked in places like Mikro.africa, Stanbic IBTC bank, Mkobo Micro finance bank as well as investment company which directly and indirectly impacted many based on feedbacks. 
I have consulted for foreign companies like Pericius, India / Tecnotree, Finland building their SAAS application with high profile clients
using this solution.

In recent time, solutions I have built from scratch are not limited to the following;
1. Transaction service processing different kind of transactions with advance even-driven architecture
2. Web service Application for Inter-bank transaction processing
3. Hybrid BVN service for BVN verification.
4. SAAS SDk for Digital Contract management.

My current role span through vendor interfacing, training developers and managing processes on bankend engineering
```
2. List of my 5 top Security Best Practises
```
1.  Use strong encryption and hashing algorithms
2.  Centralize logging and monitoring and in general use good library and keep it simple
3.  Handle sensitive data with care like using Payload validations, keep sesnitive data on secret not on config using k8s or if not 
    externalized it by keeping it in vault like harshicorp vault.
4.  Avoid keeping plain-test of sensitive data in your properties file. 
5.  USe API-Key and validation to grant difference user access and build application having scalability in mind.
```

3. Experience in Enterprise Software Architecture

```
I have experience in some of the software architecture such as Event-driven Artitecture design pattern,
microservices design, Domain-driven design. Software Architecture simply means a template for building your application at high level,
it aims to build resilience, scalable and easy to manage application. Some of these architectures might overlapps.
Sometimes there might not be clear distinction on using them seperately but surely there are best use cases for all.

Microservices is excellent, but not good for every scenerio like using it in a startup company as they might face delima in managing it at the long run.
Microservices is good for a large establishment that want to simulate it business process to be a company-in-the-box mang,aing different kind of bounded-contexts.
whereas if a Startup company not using microservice but their services must be designed having microservices in mind
like adopting extensively Event-driven artecture for building loosely-coupled applications. 
There are many use cases for software architecture but the baseline is building a roburst, scalable, manageable and testable application.
```

4. Code Review: my take
```
Code review adoption is very important in maintaining code qualities.
Even using software like Sonarqube for code quality but direct human intervention and interaction in code review cannot be 
over-emphasized as it promote team building and collaboration.

For me, what I look for in code review are but not limited to the following:
1. Application structure like naming convention, and package structure. I believe it is not only writing good code but also these play a big role in application long-term management.
Why do you need to mixed your business logic with your controller class? definately your application will be very difficult to test and manage because it is not adheing to seperation of concern or SOLID principle. 
Start from there to write SOLID code, abstraction is beautiful.
2. Miss-used of datastructure even annotations: "why do you need to use a long-list and complicated if-else statement when there are posible options to use Switch statements". 
Because your code will actually scan through all those if statement whereas switch-statement use jump-table to locate your result.
3. Memory-management.
5. logging and commenting techniques: most developer have forgotten logging and commenting processes is part of software developement, why do you need to make your code complicated for other people when you can document your process.
6. documentation and test. 
```

5. A quick dive into the mini project architecture
```
1. Async API was built to auto concurrency and thread was managed inteernally. this is about 4 times 
faster than normal popularly known sync APIs.
2. Application decouply following SOLID principles and using alot of factory pattern for seperation of concern and also 
   builder pattern and lombok was also used for pojo enrichment with hibernate validation and type52 for schema enrichement i.e no traditional setter and getter 
   and bean management made easy.
3. final keyword was used where necessary for a little bit faster variable processing this is also for request encrichement.
5. Error first check strategy was used for faster operations; "why do I need to continue request velocity when the payload is invalid?"
6. Application enchrished with logging to aid in debugging but limited implimentation was ascertained as
   just for the scope of this mini project.
7. Unit and Integration test available for roburstness and proper application understanding and management.
8. the test required fetching list of transaction without putting in place further consideration, I have built the API to accommodate
fetch by pagination for elasticity and proper API load managment. additional Optional key to the API is 

RequestParam (Integer)
** pageNo
** size
```




