
#ifndef PROJECT_CONF_H_
#define PROJECT_CONF_H_

// Set a less busy channel
#undef CC2420_CONF_CHANNEL 
#define CC2420_CONF_CHANNEL 25	

// Enable NA
#undef UIP_CONF_ND6_SEND_NA
#define UIP_CONF_ND6_SEND_NA            1

// Disable routing
#undef UIP_CONF_IPV6_RPL
#define UIP_CONF_IPV6_RPL  0

// Set different RDC or MAC, by default CSMA and contikimac (sleep mode on)
/*#undef NETSTACK_CONF_RDC
#define NETSTACK_CONF_RDC      nullrdc_driver

#undef NETSTACK_CONF_MAC
#define NETSTACK_CONF_MAC      nullmac_driver*/

// To reduce the compression threshold
//#undef SICSLOWPAN_CONF_COMPRESSION_THRESHOLD
//#define SICSLOWPAN_CONF_COMPRESSION_THRESHOLD 10

#endif /* PROJECT_CONF_H_ */
