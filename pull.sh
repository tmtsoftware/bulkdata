#!/usr/bin/env bash
echo "*********** pull on image source machine ***********"
ssh -i ../tmt-test.pem ec2-user@52.2.10.118 'cd tmt/data-transfer ; git pull --rebase'
echo "*********** pull on metrics agg machine ***********"
ssh -i ../tmt-test.pem ec2-user@52.22.19.96 'cd tmt/data-transfer ; git pull --rebase'
echo "*********** pull on metrics per second machine ***********"
ssh -i ../tmt-test.pem ec2-user@52.21.94.154 'cd tmt/data-transfer ; git pull --rebase'
for var in $@
do
    echo "*********** pull on $var machine ***********"
    ssh -i ../tmt-test.pem ec2-user@$var 'cd tmt/data-transfer ; git pull --rebase'
done
