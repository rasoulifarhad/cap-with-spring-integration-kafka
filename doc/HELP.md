##  Sample Problem
   
### Capitalize a given string.
   
> Given the input:
      
 >> "farhad"
      
> the output will be:
   
>> "Farhad"

### Architecture

Since scalability is a core goal of any System, it's usually better to design small stages focused 
on specific operations, especially if we have I/O─intensive tasks. 

Moreover, having small stages helps us better tune the scale of each stage.

To solve our Capitalize word problem, we can implement a solution with the following stages:


        ┌─────────────┐
        │  Requestor  │
        │    client   │
        └─────────────┘
           |      ^ 
           |      |
  ╔════════|══════|═══════════════════════════════════════════╗   
  ║        |      |         Edge                              ║
  ║        V      |               ┌───────────────────────┐   ║
  ║ ┌────────────────────┐        │                       │   ║
  ║ │ Capitalize Service │ -----> │ Outbout Kafka Gateway │   ║
  ║ │    ( Gateway )     │ <----- │                       │   ║
  ║ └────────────────────┘        └───────────────────────┘   ║
  ║                                     |          ^          ║
  ╚═════════════════════════════════════|══════════|══════════╝
                                        |          |
                                        |          |
                                        V  Kafka   |
                                      ________________                                   
                                     ()______________()    

                                        |         ^
                                        |         |
                                        |         | 
                                ╔═══════|═════════|══════════════════════════════════════╗ 
                                ║       V         |   Processor                          ║
                                ║ ┌───────────────────────┐                              ║
                                ║ │                       │        ┌─────────────────┐   ║ 
                                ║ │ Inbout Kafka Gateway  │ -----> │ Capitalize word │   ║
                                ║ │                       │ <----- │   ( Service )   │   ║
                                ║ └───────────────────────┘        └─────────────────┘   ║
                                ║                                                        ║
                                ╚════════════════════════════════════════════════════════╝
                 

