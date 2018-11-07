CONTIKI_PROJECT = GPS_sensor receiver
WITH_UIP6=1
CFLAGS+=-DUIP_CONF_IPV6=1 -DWITH_UIP6=1
all: $(CONTIKI_PROJECT)

#CONTIKI = ../../../../contiki
CONTIKI = $(HOME)/contiki
include $(CONTIKI)/Makefile.include
