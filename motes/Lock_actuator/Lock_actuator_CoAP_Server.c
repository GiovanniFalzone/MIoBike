#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdio.h"

// Lock resource

// lock actuator: receives a get with "?value=true" or "?value=false" in the URL, and manages the actuator variable "locked" replaying with "value=true" or "value=false"

static int locked = 1;

void Lock_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {

	int len;
	const char value[20];
	const char *val = &value[0];

	len = REST.get_query_variable(request, "value", &val);

	// "true" received
	if (len == 4) {
		if (val[0] == 't' && val[1] == 'r' && val[2] == 'u' && val[3] == 'e') {
			locked = 1;

			sprintf((char*)buffer, "value=true");

			uint8_t length = strlen((char*)buffer);
			REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
			REST.set_header_etag(response, (uint8_t *) &length, 1);
			REST.set_response_payload(response, buffer, length);

			printf("Lock GET request handler\n");
			printf("%s\n", buffer);
		}
	}

	// "false" received
	if (len == 5) {
		if (val[0] == 'f' && val[1] == 'a' && val[2] == 'l' && val[3] == 's' && val[4] == 'e') {
			locked = 0;

			sprintf((char*)buffer, "value=false");

			uint8_t length = strlen((char*)buffer);
			REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
			REST.set_header_etag(response, (uint8_t *) &length, 1);
			REST.set_response_payload(response, buffer, length);

			printf("Lock GET request handler\n");
			printf("%s\n", buffer);
		}
	}

}


EVENT_RESOURCE(Lock_resource, "title=\"Resource\";rt=\"Lock_sensor\"", Lock_get_handler, NULL, NULL, NULL, NULL);

PROCESS(server, "CoAP Server");
AUTOSTART_PROCESSES(&server);
PROCESS_THREAD(server, ev, data){
	PROCESS_BEGIN();
	rest_init_engine();
	rest_activate_resource(&Lock_resource, "Lock");
	while(1) {
		PROCESS_WAIT_EVENT();
	}
	PROCESS_END();
}

