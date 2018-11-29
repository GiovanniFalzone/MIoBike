#! /bin/bash
MAIN_DIR=$PWD/
TARGET_ARRAY=( MIoBike_common MIoBike_Phy_Simulators MIoBike_Bike_Simulator MIoBike_IN_App MIoBike_MN_App MIoBike_BikeManager )
echo "building MIoBike"
for dir in ${TARGET_ARRAY[@]};
do
	dir=${dir%*/}
	echo "~~~~~~~~building: "${dir##*/}"~~~~~~~~"
	FULL_PATH=$MAIN_DIR${dir##*/}
	cd $FULL_PATH && mvn clean install
done


echo "end building"
cd ~
