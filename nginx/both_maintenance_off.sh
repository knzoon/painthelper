#!/bin/bash

echo "Setting painthelper and comparehelper in working mode"

sudo rm /etc/nginx/sites-enabled/maintenance.painthelper.conf
sudo rm /etc/nginx/sites-enabled/maintenance.comparehelper.conf

sudo ln -s /etc/nginx/sites-available/test.painthelper.conf /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/test.comparehelper.conf /etc/nginx/sites-enabled/

sudo systemctl restart nginx

