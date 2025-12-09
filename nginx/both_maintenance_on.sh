#!/bin/bash

echo "Setting painthelper and comparehelper in maintenance mode"

sudo rm /etc/nginx/sites-enabled/test.painthelper.conf
sudo rm /etc/nginx/sites-enabled/test.comparehelper.conf

sudo ln -s /etc/nginx/sites-available/maintenance.painthelper.conf /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/maintenance.comparehelper.conf /etc/nginx/sites-enabled/

sudo systemctl restart nginx
