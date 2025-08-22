#!/bin/bash

# Script to generate a self-signed certificate for DeliGo application
# This is for development purposes only. In production, use a certificate from a trusted CA.

echo "Generating self-signed certificate for DeliGo..."

# Generate a keystore with a self-signed certificate
keytool -genkeypair \
  -alias deligo \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore deligo.p12 \
  -validity 365 \
  -storepass deliGoPassword \
  -dname "CN=localhost, OU=DeliGo, O=DeliGo, L=City, ST=State, C=Country"

# Check if the keystore was created successfully
if [ $? -eq 0 ]; then
  echo "Certificate generated successfully: deligo.p12"
  echo "Password: deliGoPassword"
  echo ""
  echo "This certificate is for development purposes only."
  echo "In production, use a certificate from a trusted Certificate Authority."
else
  echo "Failed to generate certificate."
  exit 1
fi