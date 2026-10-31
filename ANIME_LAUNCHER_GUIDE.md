# Anime Launcher - APK kaise banaye

Is project me submodules (`glfw`, `sdl`, `mojoexec`, `NoBlocklist`) GitHub ZIP me khali aate hain, aur kuch native libraries CI me download hoti hain. Isliye sabse safe tareeka GitHub Actions hai.

1. GitHub par original repo ko **Fork** karo: https://github.com/MojoLauncher/MojoLauncher (branch `v3_openjdk`).
2. Is zip ke `overlay/` folder ki saari files apne fork me same path par upload/replace karo (Add file -> Upload files). Submodule folders ko mat chhedna.
3. Fork me **Actions** tab kholo -> `Android CI` -> **Run workflow**.
4. Build khatam hone par run page ke **Artifacts** me `app-debug` download karo. Usme `app-debug-full.apk` hogi. Phone me install karo.

Note: build ke liye internet chahiye (JRE aur libraries download hoti hain). Pehla build 15-25 minute le sakta hai.

Low-end phone par FPS: default settings me resolution kam rakha gaya hai. Agar phir bhi lag ho to Settings -> Video me Resolution scale aur game ke andar render distance kam karo.
