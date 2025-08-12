package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

@TeleOp
public class SimpleTeleOp extends LinearOpMode {
    private ColorDetector visionProcessor;
    private VisionPortal visionPortal;
    double speedFactor = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        visionProcessor = new ColorDetector();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), visionProcessor);

        telemetry.addData("Identified", visionProcessor.getSelection());
        telemetry.update();


        waitForStart();

        int left = 0;
        int middle = 0;
        int right = 0;

        for (int i = 0; i < 100; i++) {
            if (visionProcessor.getSelection() == ColorDetector.Selected.LEFT) {
                left++;
            }
            else if(visionProcessor.getSelection() == ColorDetector.Selected.MIDDLE) {
                middle++;
            }
            else if (visionProcessor.getSelection() == ColorDetector.Selected.RIGHT) {
                right++;
            }
        }

        telemetry.addData("Left:", left);
        telemetry.addData("Middle:", middle);
        telemetry.addData("Right:", right);
        telemetry.update();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = -gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator * speedFactor;;
            double frontRightPower = (y - x - rx) / denominator * speedFactor;;

            frontLeftMotor.setPower(frontLeftPower);
            frontRightMotor.setPower(frontRightPower);
        }
    }
}