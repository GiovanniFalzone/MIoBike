/*#include <stdio.h>
#include "contiki.h"
#include "dev/button-sensor.h"
#include "dev/light-sensor.h"
#include "dev/leds.h"
#include <stdio.h>


PROCESS(test_button_process, "Test button");
AUTOSTART_PROCESSES(&test_button_process);
//
static uint8_t active;
PROCESS_THREAD(test_button_process, ev, data)
{
  PROCESS_BEGIN();
  active = 0;
  SENSORS_ACTIVATE(button_sensor);

  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event &&
			     data == &button_sensor);
    leds_toggle(LEDS_ALL);
    if(!active) {
      // activate light sensor
      SENSORS_ACTIVATE(light_sensor);
      printf("Light: %d\n", light_sensor.value(0));
    } else {
      // deactivate light sensor
      printf("Light: %d\n", light_sensor.value(0));
      SENSORS_DEACTIVATE(light_sensor);
    }
    active ^= 1;
    leds_toggle(LEDS_ALL);
  }
  PROCESS_END();
}
*/
/*---------------------------------------------------------------------------*/

/*
 * \file
 *          Temperature sensor simulation for bike system
 *            tempterature must be between:
 *              0:40 °C 
 *              
 * \author
 *         Alessandro Degiovanni
 */

#include "contiki.h"

#include "sys/etimer.h"

#include <stdio.h> /* For printf() */
#include <stdlib.h>

#define TMP_MIN 0
#define TMP_MAX 40

/*---------------------------------------------------------------------------*/
PROCESS(temperature_sensor,"temperature sensor process");
AUTOSTART_PROCESSES(&temperature_sensor);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(temperature_sensor, ev, data)
{
	PROCESS_BEGIN();
	static struct etimer et;
	static int temp = 8;

	// reads every 20 seconds
	etimer_set(&et, CLOCK_SECOND*20);

	while (1) {

		PROCESS_WAIT_EVENT();

		if (etimer_expired(&et)) {
			temp = (temp + 7) % 40;

			printf("temperature value: %d\n\n", temp);

			etimer_reset(&et);

		}

	}

	PROCESS_END();
}

/*---------------------------------------------------------------------------*/

