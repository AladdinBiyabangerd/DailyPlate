"""Real Android build/lint/device smoke checks. Requires an already booted emulator."""
import os
from pathlib import Path
import subprocess
import sys
import xml.etree.ElementTree as ET

from verify_tokens import check


def main() -> None:
    root = Path(__file__).resolve().parents[2]
    check(root)
    env = os.environ.copy()
    if not env.get('JAVA_HOME') and sys.platform == 'darwin':
        env['JAVA_HOME'] = subprocess.check_output(
            ['/usr/libexec/java_home', '-v', '21'], text=True).strip()
    sdk = Path(env.get('ANDROID_HOME') or env.get('ANDROID_SDK_ROOT')
               or Path.home() / 'Library/Android/sdk')
    if not (sdk / 'platforms/android-36/android.jar').exists():
        raise RuntimeError('Install Android SDK Platform 36; set ANDROID_HOME.')
    env['ANDROID_HOME'] = str(sdk)
    devices = subprocess.check_output([str(sdk / 'platform-tools/adb'), 'devices'], text=True)
    if not any(line.endswith('\tdevice') for line in devices.splitlines()):
        raise RuntimeError('Boot an Android API 24+ emulator before local CI; device smoke is required.')
    subprocess.run([
        './gradlew', '--offline', '--no-daemon', '--console=plain',
        ':app:lintDebug', ':app:assembleDebug', ':app:connectedDebugAndroidTest',
    ], cwd=root / 'android', env=env, check=True)
    reports = list((root / 'android/app/build/outputs/androidTest-results/connected').rglob('TEST-*.xml'))
    if not reports:
        raise RuntimeError('No device test report was produced.')
    smoke_ran = False
    for report in reports:
        suite = ET.parse(report).getroot()
        if any(int(suite.get(key, '0')) for key in ('failures', 'errors', 'skipped')):
            raise RuntimeError(f'Failed or skipped device tests: {report}')
        smoke_ran |= any(case.get('classname') == 'az.qida.app.LaunchSmokeTest'
                         for case in suite.findall('testcase'))
    if not smoke_ran:
        raise RuntimeError('LaunchSmokeTest did not execute.')
    apk = root / 'android/app/build/outputs/apk/debug/app-debug.apk'
    badging = subprocess.check_output([
        str(sdk / 'build-tools/36.0.0/aapt'), 'dump', 'badging', str(apk),
    ], text=True)
    for expected in ("sdkVersion:'24'", "targetSdkVersion:'36'", "compileSdkVersion='36'"):
        if expected not in badging:
            raise RuntimeError(f'APK SDK contract missing: {expected}')
    print('PASS: APK minSdk 24 / targetSdk 36')
    print('PASS: Android lint, Kotlin compilation, APK build and device launch/recreation smoke')


if __name__ == '__main__':
    main()
