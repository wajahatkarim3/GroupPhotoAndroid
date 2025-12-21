<div align="center"><img src="app/designs/allscreens.png"/></div>
<h1 align="center">GroupSnap</h1>
<h4 align="center">An Android app that solves the "missing photographer" problem by intelligently merging a group photo with a separate photo of the person who took it, making it appear as if everyone was in the same shot.</h4>
<div align="center"><a href="https://twitter.com/intent/tweet?url=https%3A%2F%2Fgithub.com%2Fwajahatkarim3%2FEasyFlipViewPager&text=Create%20amazing%20book%20or%20card%20flipping%20animations%20for%20your%20ViewPager%20in%20Android%20with%20these%202-lines%20of%20code%20through%20EasyFlipViewPager&hashtags=android%2C%20kotlin%2C%20java%2C%20opensource%2C%20programming">
        <img src="https://img.shields.io/twitter/url/http/shields.io.svg?style=social"/>
    </a> <a href="https://twitter.com/WajahatKarim">
        <img src="https://img.shields.io/twitter/follow/WajahatKarim?style=social"/>
    </a>
</div> 
<br/>

<div align="center">
    <!-- PRs Welcome -->
    <a href="">
        <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg"/>
    </a>
    <!-- Say Thanks! -->
    <a href="https://saythanks.io/to/wajahatkarim3">
        <img src="https://img.shields.io/badge/Say%20Thanks-!-1EAEDB.svg"/>
    </a>
</div>

<div align="center">
  <sub>Built with ❤︎ by
  <a href="https://wajahatkarim.com">Wajahat Karim</a> and
  <a href="https://github.com/wajahatkarim3/GroupPhotoAndroid/graphs/contributors">
    contributors
  </a>
</div>
<br/>
<br/>

## Demo Videos

##### Demo 1

https://github.com/user-attachments/assets/e204fa7e-8504-4079-8533-4bc5c6859d72

##### Demo 2

https://github.com/user-attachments/assets/fc12f657-9987-41b0-84a9-47471aed85fd

## Features

- **Smart Photo Merging** - Gemini Nano Banana AI-powered blending of photographer into group photos
- **Quick Share** - One-tap sharing to Stories, WhatsApp, Messages, and more
- **Before/After Compare** - Hold to compare original vs merged photo

## Designs

Designs are created with the Google Stitch https://stitch.withgoogle.com/projects/9440880816474808086

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Gemini API (for AI merging and accessing Nano Banana)

## Getting Started

1. Clone the repository
2. Get your Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey)
3. Add the API key to your `local.properties` file:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```
4. Open in Android Studio
5. Sync Gradle files
6. Run on device/emulator (minSdk 26)

## Project Structure

```
app/src/main/java/com/wajahatkarim/groupphotos/
├── MainActivity.kt
└── ui/
    ├── navigation/
    │   └── AppNavigation.kt
    ├── screens/
    │   ├── HomeScreen.kt
    │   ├── GroupPhotoUploadScreen.kt
    │   ├── PhotographerUploadScreen.kt
    │   ├── ProcessingScreen.kt
    │   └── ResultScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## License

Apache 2 License
