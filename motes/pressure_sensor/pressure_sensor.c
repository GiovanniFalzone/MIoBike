/**
 * \file
 *          Tyre pressure sensor simulation for bike system
 *            pressure must be between:
 *              80-130 PSI (Pounds per Square Inch)
 *              5.5 - 9 bar
 *            divide for 14,5 to convert
 *              
 * \author
 *         Roberto Ciardi
 */

#include "contiki.h"

#include "sys/etimer.h"

#include <stdio.h> /* For printf() */
#include <stdlib.h>

#define PRESS_MAX 130
#define PRESS_MIN 80

/*---------------------------------------------------------------------------*/
PROCESS(pressure_sensor,"Pressure sensor process");
AUTOSTART_PROCESSES(&pressure_sensor);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(pressure_sensor, ev, data)
{
  PROCESS_BEGIN();
  static struct etimer et;
  static int p_PSI = PRESS_MAX;

  // the pressure is revealed every 10 seconds
  etimer_set(&et, CLOCK_SECOND*10);

  while(1){

  // wait for the timer
	PROCESS_WAIT_EVENT();

	if(etimer_expired(&et)){
    p_PSI = p_PSI - 1;
    if(p_PSI < PRESS_MIN) {
      p_PSI = PRESS_MAX;
    }
    int p_bar = p_PSI/14;
    printf("Pressure value: \n\t%d PSI\n\t%d bar\n",p_PSI, p_bar);


		etimer_reset(&et);

	}

  }

  
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
