#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdio.h"

// TyrePressure resource

// returns, through a pseudo random like algorithm, the front and rear pressure (measured bar)
// between (PRESS0 - (MAX_PRESS_OSCILL-1)/2)/1000 and (PRESS0 + (MAX_PRESS_OSCILL-1)/2)/1000

#define PRESS0 2500
#define MAX_PRESS_OSCILL 3001

static int press1;
static int press2;

void pressure_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	int int_p1 = press1 / 1000;
	int dec_p1 = press1 % 1000;
	int int_p2 = press2 / 1000;
	int dec_p2 = press2 % 1000;
	sprintf((char*)buffer, "{\"format\":\"bar\", \"value\":{\"front\":\"%d.%d\", \"rear\":\"%d.%d\"} }",
		int_p1, dec_p1, int_p2, dec_p2);
	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("TyrePressure GET request handler\n");
	printf("%s\n", buffer);
}

EVENT_RESOURCE(pressure_resource, "title=\"Resource\";rt=\"pressure_sensor\"", pressure_get_handler,
	NULL, NULL, NULL, NULL);

PROCESS(server, "CoAP Server");
AUTOSTART_PROCESSES(&server);
PROCESS_THREAD(server, ev, data){

	PROCESS_BEGIN();
	static struct etimer et;

	static int pseudo_rand1;
	static int pseudo_rand2;

	// every 10 seconds
	etimer_set(&et, CLOCK_SECOND*10);

	rest_init_engine();
	rest_activate_resource(&pressure_resource, "TyrePressure");

	while (1) {

		PROCESS_WAIT_EVENT();

		if (etimer_expired(&et)) {
			// pseudo casual like generation (with small numbers since we are only simulating the sensor data)
			int big_prime1 = 3389;
			int small_prime1 = 3253;
			int a = 3;
			pseudo_rand1 = (a*pseudo_rand1 + small_prime1) % big_prime1;

			press1 = pseudo_rand1 % MAX_PRESS_OSCILL;
			press1 = press1 + PRESS0 - (MAX_PRESS_OSCILL-1) / 2;

			// pseudo casual like generation (with small numbers since we are only simulating the sensor data)
			int big_prime2 = 2417;
			int small_prime2 = 2293;
			pseudo_rand2 = (a*pseudo_rand2 + small_prime2) % big_prime2;

			press2 = pseudo_rand2 % MAX_PRESS_OSCILL;
			press2 = press2 + PRESS0 - (MAX_PRESS_OSCILL-1) / 2;

			etimer_reset(&et);
		}
	}
	PROCESS_END();
}

