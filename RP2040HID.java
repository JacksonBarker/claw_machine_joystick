package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@I2cDeviceType
@DeviceProperties(name = "RP2040 HID", description = "Raspberry Pi Pico 4-Bit HID", xmlTag = "RP2040HID")
public class RP2040HID extends I2cDeviceSynchDevice<I2cDeviceSynch>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // User Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public double X()
    {
        byte data = deviceClient.read(0x00, 2)[0];
        switch (data) {
            case 0x07: // 1000 North
                return 1.0;
            case 0x0D: // 0010 South
                return -1.0;
            case 0x0F: // 0000 Middle
            default:
                return 0.0;
        }
    }

    public double Y()
    {
        byte data = deviceClient.read(0x00, 2)[0];
        switch (data) {
            case 0x0B: // 0100 East
                return 1.0;
            case 0x0E: // 0001 West
                return -1.0;
            case 0x0F: // 0000 Middle
            default:
                return 0.0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction and Initialization
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static I2cAddr ADDRESS_I2C_DEFAULT = I2cAddr.create7bit(0x18);

    public RP2040HID(I2cDeviceSynch deviceClient)
    {
        super(deviceClient, true);

        this.setOptimalReadWindow();
        this.deviceClient.setI2cAddress(ADDRESS_I2C_DEFAULT);

        super.registerArmingStateCallback(false); // Deals with USB cables getting unplugged
        // Sensor starts off disengaged so we can change things like I2C address. Need to engage
        this.deviceClient.engage();
    }

    protected void setOptimalReadWindow()
    {
        // Sensor registers are read repeatedly and stored in a register. This method specifies the
        // registers and repeat read mode
        I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(0x00, 1, I2cDeviceSynch.ReadMode.REPEAT);
        this.deviceClient.setReadWindow(readWindow);
    }

    @Override
    protected synchronized boolean doInitialize()
    {
        Byte state = deviceClient.read(0x00, 2)[0];
        for (Byte bytes : new Byte[]{0x0F, 0x07, 0x0B, 0x0D, 0x0E}) {
            if (state.equals(bytes)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Manufacturer getManufacturer()
    {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName()
    {
        return "Raspberry Pi Pico 4-Bit HID";
    }
}