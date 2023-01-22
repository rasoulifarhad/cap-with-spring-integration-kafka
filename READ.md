### Run 
#
## 1. Run Kafka
#
#  Go to `docker` folder then run `docker compose up -d` 
#
## 2. Run Edge(web)
#
#  Go to  `cap-kafka-edge-web` folder and run `./mvnw spring-boot:run`
#
## 3. Run Processor
#
#  Go to  `cap-kafka-processor` folder and run `./mvnw spring-boot:run`
#
## 4. Test with curl
#
#  curl -s -X POST -H 'Content-Type: application/json' -d 'test' -w "\n"  http://localhost:9080/api/cap
#
#  OR
#
#  curl --header "Content-Type: application/json" \
#       --request POST \
#       --data 'test' \
#       -w "\n" \
#       http://localhost:9080/api/cap
#
#   *************************************************
#   *                     DOCs                      *
#   *************************************************
#
## I. Sample Problem
#   
#     Capitalize a given string.
#   
#     Given the input:
#      
#      "farhad"
#      
#     The output will be:
#   
#      "Farhad"
#
## II. Architecture
#
#      Since scalability is a core goal of any System, it's usually better to design small stages focused 
#      on specific operations, especially if we have I/O─intensive tasks. 
#
#      Moreover, having small stages helps us better tune the scale of each stage.
#
#      To solve our Capitalize word problem, we can implement a solution with the following stages:
#
#        ┌─────────────┐ 
#        │  Requestor  │
#        │  (Client)   │
#        └─────────────┘
#           |      ^ 
#           |      |
#  *********|******|********************************************   
#  *        |      |         Edge                              * 
#  *        V      |                ┌───────────────────────┐  *
#  *  ┌────────────────────┐        │                       │  *
#  *  │ Capitalize Service │ -----> │ Outbout Kafka Gateway │  *
#  *  │    ( Gateway )     │ <----- │                       │  *
#  *  └────────────────────┘        └───────────────────────┘  *
#  *                                     |          ^          *
#  **************************************|**********|***********
#                                        |          |
#                                        |          |
#                                        V  Kafka   |
#                                      ________________                                   
#                                     ()______________()    
#
#                                        |         ^
#                                        |         |
#                                        |         | 
#                                ********|*********|*************************************** 
#                                *       V         |   Processor                          *
#                                *  ┌───────────────────────┐                             *
#                                *  │                       │        ┌─────────────────┐  *  
#                                *  │ Inbout Kafka Gateway  │ -----> │ Capitalize word │  *
#                                *  │                       │ <----- │   ( Service )   │  *
#                                *  └───────────────────────┘        └─────────────────┘  * 
#                                *                                                        *
#                                **********************************************************
#                 

