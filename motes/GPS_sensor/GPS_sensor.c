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

#define SAMPLE_PERIOD	60	// every minute
#define SEND_PERIOD 	30

struct GPS_DATA{
	long int latitude;
	long int longitude;
};


/*---------------------------------------------------------------------------*/
PROCESS(GPS_sensor_process, "I'm a GPS_sensor");
AUTOSTART_PROCESSES(&GPS_sensor_process);
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

static struct GPS_DATA read_GPS_data(){
	static unsigned int iteration = 0;
	iteration++;
	struct GPS_DATA GPS_read;
	GPS_read.latitude = 43708530 + iteration;
	GPS_read.longitude = 10403600 + iteration;
	return GPS_read;
}

static void send_GPS_sensor_data(struct GPS_DATA GPS_data){
	uip_ipaddr_t addr;
	static struct simple_udp_connection broadcast_connection;
	simple_udp_register(&broadcast_connection, UDP_PORT, NULL, UDP_PORT, receiver);
	uip_create_linklocal_allnodes_mcast(&addr);
	simple_udp_sendto(&broadcast_connection, (void*)&GPS_data, sizeof(GPS_data), &addr);
	printf("Sended GPS_read latitude: %ld longitude: %ld\n", GPS_data.latitude, GPS_data.longitude);
}

static void main_iteration(uip_ipaddr_t ipaddr, struct etimer send_timer){
	struct GPS_DATA GPS_read = read_GPS_data();
	leds_toggle(LEDS_ALL);
	if(etimer_expired(&send_timer)) {
		send_GPS_sensor_data(GPS_read);
	}
}

PROCESS_THREAD(GPS_sensor_process, ev, data) {
	static struct etimer periodic_timer;
	static struct etimer send_timer;

	PROCESS_BEGIN();
	etimer_set(&periodic_timer, CLOCK_SECOND*SAMPLE_PERIOD);
	etimer_set(&send_timer, CLOCK_SECOND*SEND_PERIOD);
	uip_ipaddr_t ipaddr = set_address();
	print_addresses(ipaddr);

	while(1){
		PROCESS_WAIT_EVENT();
		if(etimer_expired(&periodic_timer)) {
			etimer_reset(&periodic_timer);
			main_iteration(ipaddr, send_timer);
		}
	}
	PROCESS_END();
}
/*---------------------------------------------------------------------------*/
