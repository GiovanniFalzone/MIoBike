# CONTIKI_PROJECT = GPS_sensor receiver
# WITH_UIP6=1
# CFLAGS+=-DUIP_CONF_IPV6=1 -DWITH_UIP6=1
# all: $(CONTIKI_PROJECT)

# CONTIKI = ../../../../contiki
# include $(CONTIKI)/Makefile.include

all: GPS_sensor_CoAP_Server
SMALL=1
CONTIKI=$(HOME)/contiki
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
APPS += er-coap
APPS += rest-engine
CONTIKI_WITH_IPV6 = 1
include $(CONTIKI)/Makefile.include