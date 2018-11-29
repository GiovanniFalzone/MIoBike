all: TyrePressure_sensor_CoAP_Server
SMALL=1
CONTIKI=$(HOME)/contiki
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
APPS += er-coap
APPS += rest-engine
CONTIKI_WITH_IPV6 = 1
include $(CONTIKI)/Makefile.include

