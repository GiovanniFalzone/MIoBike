#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdlib.h"
/* Resource definition */
void GPS_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	#ifdef DEBUG
		printf("POST request handler\n");
		printf(buffer);
		printf("\n");
	#endif	
}
void GPS_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
/* Populat the buffer with the response payload*/
	sprintf((char*)buffer, "{\"name\":\"GPS\", \"value\":{\"lat\":43.7158, \"long\":10.3987} }");
	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);
	#ifdef DEBUG
		printf("GET request handler\n");
		printf(buffer);
		printf("\n");
	#endif
}

EVENT_RESOURCE(GPS_resource, "title=\"Resource\";rt=\"GPS_sensor\"", GPS_get_handler, NULL, NULL, NULL, NULL);

PROCESS(server, "CoAP Server");
AUTOSTART_PROCESSES(&server);
PROCESS_THREAD(server, ev, data){
	PROCESS_BEGIN();
	rest_init_engine();
	rest_activate_resource(&GPS_resource, "GPS");
	while(1) {
		PROCESS_WAIT_EVENT();
	}
	PROCESS_END();
}