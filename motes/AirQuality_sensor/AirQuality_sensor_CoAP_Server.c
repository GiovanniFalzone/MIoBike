#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdio.h"

// AirQuality resource

// returns, through a pseudo random like algorithm, a PM10 concentration value (measured in µg/m^3)
// between AIRQUAL0 - (MAX_AIRQUAL_OSCILL-1)/2 and AIRQUAL0 + (MAX_AIRQUAL_OSCILL-1)/2

#define AIRQUAL0 15 // mean value
#define MAX_AIRQUAL_OSCILL 5

static int airqual;

void AirQuality_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	sprintf((char*)buffer, "{\"format\":\"mge-3/m^3\", \"value\":\"%d\" }", airqual);
	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("AirQuality GET request handler\n");
	printf("%s\n", buffer);
}

EVENT_RESOURCE(AirQuality_resource, "title=\"Resource\";rt=\"AirQuality_sensor\"", AirQuality_get_handler, NULL, NULL, NULL, NULL);

PROCESS(server, "CoAP Server");
AUTOSTART_PROCESSES(&server);
PROCESS_THREAD(server, ev, data){

	PROCESS_BEGIN();
	static struct etimer et;
	static int pseudo_rand;

	// every 10 seconds
	etimer_set(&et, CLOCK_SECOND*10);

	rest_init_engine();
	rest_activate_resource(&AirQuality_resource, "AirQuality");

	while (1) {

		PROCESS_WAIT_EVENT();

		if (etimer_expired(&et)) {
			// pseudo casual like generation (with small numbers since we are only simulating the sensor data)
			int big_prime = 3389;
			int small_prime = 3253;
			int a = 3;
			pseudo_rand = (a*pseudo_rand + small_prime) % big_prime;

			airqual = pseudo_rand % MAX_AIRQUAL_OSCILL;
			airqual = airqual + AIRQUAL0 - (MAX_AIRQUAL_OSCILL-1) / 2;

			etimer_reset(&et);
		}
	}
	PROCESS_END();
}

