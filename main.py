from machine import Pin
from i2c_responder import I2CResponder

SWITCH_NORTH = 6
SWITCH_EAST  = 7
SWITCH_SOUTH = 8
SWITCH_WEST  = 9

RESPONDER_ADDRESS = 0x41
GPIO_RESPONDER_SDA = 0
GPIO_RESPONDER_SCL = 1

pins = [Pin(SWITCH_NORTH, Pin.IN, Pin.PULL_UP),
        Pin(SWITCH_EAST,  Pin.IN, Pin.PULL_UP),
        Pin(SWITCH_SOUTH, Pin.IN, Pin.PULL_UP),
        Pin(SWITCH_WEST,  Pin.IN, Pin.PULL_UP)]

def main():
    if  GPIO_RESPONDER_SDA in [0, 4, 8, 12, 16, 20] and (
        GPIO_RESPONDER_SCL in [1, 5, 9, 13, 17, 21]):
        RESPONDER_I2C_DEVICE_ID = 0
    elif GPIO_RESPONDER_SDA in [2, 6, 10, 14, 18, 26] and (
         GPIO_RESPONDER_SCL in [3, 7, 11, 15, 19, 27]):
        RESPONDER_I2C_DEVICE_ID = 1
    else:
        exit(1)

    i2c_responder = I2CResponder(
       RESPONDER_I2C_DEVICE_ID,
       sda_gpio=GPIO_RESPONDER_SDA,
       scl_gpio=GPIO_RESPONDER_SCL,
       responder_address=RESPONDER_ADDRESS
    )
    while True:
        buffer = ""
        for pin in pins:
            buffer += str(pin.value())
        buffer_out = bytes([int(buffer, 2)])
        while not i2c_responder.read_is_pending():
            pass
        i2c_responder.put_read_data(buffer_out)

if __name__ == "__main__":
    main()