#include "contiki.h"

#include "dev/button-sensor.h"
#include "dev/leds.h"
#include "sys/etimer.h"

#include <stdio.h> /* For printf() */
#include <string.h>

#include "net/ip/uip.h"
#include "net/ip/uip-debug.h"
#include "net/ipv6/uip-ds6.h"
#include "simple-udp.h"

#define UDP_PORT 1234

#undef CC2420_CONF_CHANNEL
#define CC2420_CONF_CHANNEL	20

#define SAMPLING_PERIOD	10	// every minute
#define SENDING_PERIOD 	30

struct Sensor_DATA{
	long int latitude;
	long int longitude;
};


/*---------------------------------------------------------------------------*/
PROCESS(Sensor_sensor_process, "I'm a Sensor_sensor");
AUTOSTART_PROCESSES(&Sensor_sensor_process);
/*---------------------------------------------------------------------------*/

static void receiver(struct simple_udp_connection *c, const uip_ipaddr_t *sender_addr, uint16_t sender_port, const uip_ipaddr_t *receiver_addr, uint16_t receiver_port, const uint8_t *data, uint16_t datalen) {
  printf("Data received from ");
  uip_debug_ipaddr_print(sender_addr);
  printf(" on port %d from port %d with length %d, Received::\n", receiver_port, sender_port, datalen);
  printf(data);
}


static uip_ipaddr_t set_address(void){
	uip_ipaddr_t ipaddr;
	uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
	uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
	uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);
	return ipaddr;
}

static void print_addresses(uip_ipaddr_t ipaddr) {
	int i;
	uint8_t state;
	printf("IPv6 addresses: ");
	for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
		state = uip_ds6_if.addr_list[i].state;
		if(uip_ds6_if.addr_list[i].isused &&
			 (state == ADDR_TENTATIVE || state == ADDR_PREFERRED)) {
			uip_debug_ipaddr_print(&uip_ds6_if.addr_list[i].ipaddr);
			printf("\n");
		}
	}
}

static struct Sensor_DATA read_Sensor_data(){
	static unsigned int iteration = 0;
	iteration++;
	struct Sensor_DATA Sensor_read;
	Sensor_read.latitude = 43708530 + iteration;
	Sensor_read.longitude = 10403600 + iteration;
	return Sensor_read;
}

static void send_Sensor_sensor_data(struct Sensor_DATA Sensor_data){
	uip_ipaddr_t addr;
	static struct simple_udp_connection broadcast_connection;
// move outside
	simple_udp_register(&broadcast_connection, UDP_PORT, NULL, UDP_PORT, receiver);
	uip_create_linklocal_allnodes_mcast(&addr);
//	big endian little endian problem
	simple_udp_sendto(&broadcast_connection, (void*)&Sensor_data, sizeof(Sensor_data), &addr);
	printf("GPS_read latitude: %ld longitude: %ld\n", Sensor_data.latitude, Sensor_data.longitude);
}

static void main_iteration(uip_ipaddr_t ipaddr, struct etimer send_period){
	static unsigned int iteration = 0;
	iteration++;
	printf("iteration: %d\n", iteration);
	struct Sensor_DATA Sensor_read = read_Sensor_data();
	leds_toggle(LEDS_ALL);
	if(etimer_expired(&send_period)) {
		send_Sensor_sensor_data(Sensor_read);
	}
}

PROCESS_THREAD(Sensor_sensor_process, ev, data) {
	static struct etimer task_period;
	static struct etimer send_period;

	PROCESS_BEGIN();
	etimer_set(&task_period, CLOCK_SECOND*SAMPLING_PERIOD);
	etimer_set(&send_period, CLOCK_SECOND*SENDING_PERIOD);
	uip_ipaddr_t ipaddr = set_address();
	print_addresses(ipaddr);

	while(1){
		PROCESS_WAIT_EVENT();
		if(etimer_expired(&task_period)) {
			etimer_reset(&task_period);
			main_iteration(ipaddr, send_period);
		}
		if(etimer_expired(&send_period)) {
			etimer_reset(&send_period);
		}
	}
	PROCESS_END();
}
/*---------------------------------------------------------------------------*/
