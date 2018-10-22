CONTIKI_PROJECT = pressure_sensor receiver
all: $(CONTIKI_PROJECT)

APPS=servreg-hack
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
CFLAGS+= -DUIP_CONF_IPV6=1 -DWITH_UIP6=1

CONTIKI=../../../../contiki

WITH_UIP6=1
UIP_CONF_IPV6=1


include $(CONTIKI)/Makefile.include

