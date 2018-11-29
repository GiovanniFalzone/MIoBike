#include "contiki.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include "stdio.h"

// GPS resource, Odometer resource, Speed resource

static char latitude[6]; // decimal digits of latitude
static char longitude[6]; // decimal digits of longitude
static char total_distance_km[5]; // covered km
static char speed[2]; // actual speed

static int post_invoked = 0; // boolean saying if the POST has been invoked at least one time

// GPS post handler: updates GPS, Odometer and Speed values
void GPS_post_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {

	int i;
	int len;
	const char value[50];
	const char *val = &value[0];

	len = REST.get_post_variable(request, "lat", &val);

	if (len == 40) { // to respect the format
		// GPS
		printf("GPS POST REQUEST: %s \n", val);
		for (i = 0; i < 6; i++) {
			char c = val[i+3];
			latitude[i] = c;
		}

		printf("received latitude: %c%c%c%c%c%c\n", latitude[0], latitude[1], latitude[2], latitude[3],
			latitude[4], latitude[5]);

		for (i = 0; i < 6; i++) {
			char c = val[i+18];
			longitude[i] = c;
		}

		printf("received longitude: %c%c%c%c%c%c\n", longitude[0], longitude[1], longitude[2], longitude[3],
			longitude[4], longitude[5]);

		// Odometer
		for (i = 0; i < 5; i++) {
			char c = val[i+28];
			total_distance_km[i] = c;
		}

		printf("received total_distance_km: %c%c%c%c%c\n", total_distance_km[0], total_distance_km[1],
			total_distance_km[2], total_distance_km[3], total_distance_km[4]);

		// Speed
		for (i = 0; i < 2; i++) {
			char c = val[i+38];
			speed[i] = c;
		}

		printf("received speed: %c%c\n", speed[0], speed[1]);

	}

	post_invoked = 1;
}

void GPS_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	if (post_invoked == 0) // default position is Pisa Centrale station
		sprintf((char*)buffer, "{\"name\":\"GPS\", \"value\":{\"lat\":\"43.708894\", \"long\":\"10.398309\"} }");
	else
		sprintf((char*)buffer,
			"{\"name\":\"GPS\", \"value\":{\"lat\":\"43.%c%c%c%c%c%c\", \"long\":\"10.%c%c%c%c%c%c\"} }",
			latitude[0], latitude[1], latitude[2], latitude[3], latitude[4], latitude[5], longitude[0],
			longitude[1], longitude[2], longitude[3], longitude[4], longitude[5]);

	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("GPS GET request handler\n");
	printf("%s\n", buffer);
}

void Odometer_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	if (post_invoked == 0) // default total_km = 0
		sprintf((char*)buffer, "{\"format\":\"km\", \"value\":\"00000\" }");
	else
		sprintf((char*)buffer, "{\"format\":\"km\", \"value\":\"%c%c%c%c%c\" }", total_distance_km[0],
			total_distance_km[1], total_distance_km[2], total_distance_km[3], total_distance_km[4]);

	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("Odometer GET request handler\n");
	printf("%s\n", buffer);
}

void Speed_get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	if (post_invoked == 0) // default speed = 0
		sprintf((char*)buffer, "{\"format\":\"km/h\", \"value\":\"00\" }");
	else
		sprintf((char*)buffer, "{\"format\":\"km/h\", \"value\":\"%c%c\" }", speed[0], speed[1]);

	uint8_t length = strlen((char*)buffer);
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
	REST.set_header_etag(response, (uint8_t *) &length, 1);
	REST.set_response_payload(response, buffer, length);

	printf("Speed GET request handler\n");
	printf("%s\n", buffer);
}

EVENT_RESOURCE(GPS_resource, "title=\"GPS_Resource\";rt=\"GPS_sensor\"", GPS_get_handler, GPS_post_handler,
	NULL, NULL, NULL);
EVENT_RESOURCE(Odometer_resource, "title=\"Odometer_Resource\";rt=\"Odometer_sensor\"", Odometer_get_handler,
	NULL, NULL, NULL, NULL);
EVENT_RESOURCE(Speed_resource, "title=\"Speed_Resource\";rt=\"Speed_sensor\"", Speed_get_handler, NULL,
	NULL, NULL, NULL);

PROCESS(server, "CoAP Server");
AUTOSTART_PROCESSES(&server);
PROCESS_THREAD(server, ev, data){
	PROCESS_BEGIN();
	rest_init_engine();
	rest_activate_resource(&GPS_resource, "GPS");
	rest_activate_resource(&Odometer_resource, "Odometer");
	rest_activate_resource(&Speed_resource, "Speed");
	while(1) {
		PROCESS_WAIT_EVENT();
	}
	PROCESS_END();
}

