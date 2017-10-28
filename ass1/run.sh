#!/bin/bash

USER_ONE=rsmohamad**********I
PASS_ONE=_________1_________1_________1Irsmohamad**********I_________1_________1_________1I

USER_TWO=rsmohamad**********I
PASS_TWO=_________1_________1_________1aaC^ssrsmohamad**********I_________1_________1_________1

USER_THREE=rsmohamad**********I
PASS_THREE=_________1_________1_________1rsmohamad**********I_________1_________1_________1

echo "Running the login program ..."
echo ""
echo "First method"
./login -i $USER_ONE $PASS_ONE
echo ""
echo "Second method"
./login -j $USER_TWO $PASS_TWO
echo ""
echo "Third method"
./login -k $USER_THREE $PASS_THREE

echo $USER_ONE > a1a.txt
echo $PASS_ONE >> a1a.txt

echo $USER_TWO > a1b.txt
echo $PASS_TWO >> a1b.txt

echo $USER_THREE > a1c.txt
echo $PASS_THREE >> a1c.txt
