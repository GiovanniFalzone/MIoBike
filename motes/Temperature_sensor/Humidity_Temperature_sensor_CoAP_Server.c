#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdio.h"

// Humidity resource and Temperature resource

// returns, through a pseudo random like algorithm, a relative humidity value (percentual)
// between HUM0 - (MAX_HUM_OSCILL-1)/2 and HUM0 + (MAX_HUM_OSCILL-1)/2

// returns, through a pseudo random like algorithm, a temperature value
// between TEMP0 - (MAX_TEMP_OSCILL-1)/2 and TEMP0 + (MAX_TEMP_OSCILL-1)/2

#define TEMP0 20 // mean value
#define MAX_TEMP_OSCILL 11

#define HUM0 70 // mean value
#define MAX_HUM_OSCILL 41

static int temp;
static int hum;

void Temperature_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	sprintf((char*)buffer, "{\"format\":\"C\", \"value\":\"%d\" }", temp);

	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("Temperature GET request handler\n");
	printf("%s\n", buffer);
}

void Humidity_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	sprintf((char*)buffer, "{\"format\":\"Relative Humidity (percent)\", \"value\":\"%d\" }", hum);

	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("Humidity GET request handler\n");
	printf("%s\n", buffer);
}

EVENT_RESOURCE(Temperature_resource, "title=\"Resource\";rt=\"temperature_sensor\"", Temperature_get_handler,
	NULL, NULL, NULL, NULL);

EVENT_RESOURCE(Humidity_resource, "title=\"Resource\";rt=\"Humidity_sensor\"", Humidity_get_handler,
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
	rest_activate_resource(&Temperature_resource, "Temperature");
	rest_activate_resource(&Humidity_resource, "Humidity");

	while (1) {

		PROCESS_WAIT_EVENT();

		if (etimer_expired(&et)) {
			// pseudo casual like generation (with small numbers since we are only simulating the sensor data)
			int big_prime1 = 3389;
			int small_prime1 = 3253;
			int a = 3;
			pseudo_rand1 = (a*pseudo_rand1 + small_prime1) % big_prime1;

			temp = pseudo_rand1 % MAX_TEMP_OSCILL;
			temp = temp + TEMP0 - (MAX_TEMP_OSCILL-1) / 2;

			// pseudo casual like generation (with small numbers since we are only simulating the sensor data)
			int big_prime2 = 2417;
			int small_prime2 = 2293;
			pseudo_rand2 = (a*pseudo_rand2 + small_prime2) % big_prime2;

			hum = pseudo_rand2 % MAX_HUM_OSCILL;
			hum = hum + HUM0 - (MAX_HUM_OSCILL-1) / 2;

			etimer_reset(&et);

		}
	}
	PROCESS_END();
}

