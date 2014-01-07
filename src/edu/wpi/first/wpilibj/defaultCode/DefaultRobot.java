/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.defaultCode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;

public class DefaultRobot extends IterativeRobot {
	RobotDrive m_robotDrive;
	int m_dsPacketsReceivedInCurrentSecond;
	Joystick m_rightStick;
	Joystick m_leftStick;
	static final int NUM_JOYSTICK_BUTTONS = 16;
	boolean[] m_rightStickButtonState = new boolean[(NUM_JOYSTICK_BUTTONS+1)];
	boolean[] m_leftStickButtonState = new boolean[(NUM_JOYSTICK_BUTTONS+1)];
	static final int NUM_SOLENOIDS = 8;
	Solenoid[] m_solenoids = new Solenoid[NUM_SOLENOIDS];
	static final int UNINITIALIZED_DRIVE = 0;
	static final int ARCADE_DRIVE = 1;
	static final int TANK_DRIVE = 2;
	int m_driveMode;
	int m_autoPeriodicLoops;
	int m_disabledPeriodicLoops;
	int m_telePeriodicLoops;
        
    public DefaultRobot() {
        System.out.println("BuiltinDefaultCode Constructor Started\n");
	m_robotDrive = new RobotDrive(1, 3, 2, 4); //initialize four motor drive
	m_dsPacketsReceivedInCurrentSecond = 0;
	m_rightStick = new Joystick(1); //right joystick
	m_leftStick = new Joystick(2); //left joystick
	int buttonNum = 1; //initialize buttons on the joysticks
	for (buttonNum = 1; buttonNum <= NUM_JOYSTICK_BUTTONS; buttonNum++) {
		m_rightStickButtonState[buttonNum] = false;
		m_leftStickButtonState[buttonNum] = false;
	}
	int solenoidNum = 1; //initialize solenoids
	for (solenoidNum = 0; solenoidNum < NUM_SOLENOIDS; solenoidNum++) {
		m_solenoids[solenoidNum] = new Solenoid(solenoidNum + 1);
	}
	m_driveMode = UNINITIALIZED_DRIVE; //start drive mode in uninitialized
	m_autoPeriodicLoops = 0;
	m_disabledPeriodicLoops = 0;
	m_telePeriodicLoops = 0;
	System.out.println("BuiltinDefaultCode Constructor Completed\n");
	}
    
	public void robotInit() {
		System.out.println("RobotInit() completed.\n");
	}

	public void disabledInit() {
		m_disabledPeriodicLoops = 0;
        startSec = (int)(Timer.getUsClock() / 1000000.0);
		printSec = startSec + 1;
	}

	public void autonomousInit() {
		m_autoPeriodicLoops = 0;
	}

	public void teleopInit() {
		m_telePeriodicLoops = 0;
		m_dsPacketsReceivedInCurrentSecond = 0;
		m_driveMode = UNINITIALIZED_DRIVE;
	}
        
	static int printSec;
	static int startSec;

	public void disabledPeriodic()  {
		Watchdog.getInstance().feed();
		m_disabledPeriodicLoops++;
		if ((Timer.getUsClock() / 1000000.0) > printSec) {
			System.out.println("Disabled seconds: " + (printSec - startSec));
			printSec++;
		}
	}

	public void autonomousPeriodic() {
		Watchdog.getInstance().feed();
		m_autoPeriodicLoops++;
		if (m_autoPeriodicLoops == 1) {
			m_robotDrive.tankDrive(0.5, 0.5); //set left, right motors to half speed for two seconds
		}
		if (m_autoPeriodicLoops == (2 * GetLoopsPerSec())) {
			// After 2 seconds, stop the robot
			m_robotDrive.tankDrive(0.0, 0.0);
		}
	}
        
        public void teleopPeriodic() {
            Watchdog.getInstance().feed();
            m_telePeriodicLoops++;
            m_dsPacketsReceivedInCurrentSecond++;
            Solenoid[] firstGroup = new Solenoid[4];
            Solenoid[] secondGroup = new Solenoid[4];
            for (int i = 0; i < 4; i++) {
                firstGroup[i] = m_solenoids[i];
                secondGroup[i] = m_solenoids[i + 4];
            }
            if (m_rightStick.getZ() <= 0) {
                m_robotDrive.arcadeDrive(m_rightStick, false);
                if (m_driveMode != ARCADE_DRIVE) {
                System.out.println("Arcade Drive\n");
                m_driveMode = ARCADE_DRIVE;
                }
            } else {
                m_robotDrive.tankDrive(m_leftStick, m_rightStick);	// drive with tank style
                if (m_driveMode != TANK_DRIVE) {
                    System.out.println("Tank Drive\n");
                    m_driveMode = TANK_DRIVE;
                }
            }
        }
        
        int GetLoopsPerSec() {
            return 20;
        }
}
