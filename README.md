# MIo Bike
## oneM2m-based IoT Bike sharing project

MIoBike is a Bike sharing service implemented for Internet of Thing.
The sensors and the actuator are implemented in contiki and also in java in order to simulate the bikes, they exposes CoAP RESTfull services used by the application running on the bike.
The application on the bikes retrieve the data from the sensors and interact with the oneM2M MN in order to mantaine a resource Tree ralative to that bike.
Another actor of the system is the om2m IN running on the cloud togheter with the application involved for the maintenance of the IN itself, an exact copy of the Bike Resource Tree on the MN is mantained in the IN, and also the last data for each sensor are copied into the resource tree of the relative bike.
A Bike Manager is running also in the cloud and his task is retrieve the data for each bike from the IN using a subscription/notification approch, the incoming data are elaborated if it is a request or are sent to the WebService running as Servlets.
The WebService is the top part of the architecture and is composed by several Servlet, the data coming from the om2m Architecture are elaborated and organized in a different DataBase;
The web application has been designed to be responsive and friendly, each user can interact with the locked bikes distributed around the city, can view the statistics relative to the usage of the system, the kilometers done and some other usefull information relative to the user
The administrator instead has a different view, he can have a look on the last bike's sensor value in real-time and can use statistics  usefull for the maintenance of the system, can have a look on the map and see where the bikes are located but also he can evaluate the chart relative to the data coming from the bikes, data as pollution, temperature, humidity and so on.

## Authors

Roberto Ciardi - www.github.com/robertociardi - robertociardi.github.io 
\
Giovanni Falzone - www.github.com/GiovanniFalzone - 
giovannifalzone.github.io \
Alessandro Degiovanni - www.github.com/degiovannialessandro 
