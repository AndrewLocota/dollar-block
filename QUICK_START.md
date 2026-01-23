# ⚡ Quick Start - Dollar Block

**Branch**: `claude/dollar-block-mockups-Wp6zC`

## 🎯 What You're Getting

A **pixel-perfect Android app** matching your mockups:
- ✅ Full black UI with serif fonts
- ✅ Onboarding → App Selection → Block → Shame
- ✅ Toggle switches for app blocking
- ✅ Progress bar with countdown
- ✅ NO payment processing (simplified for testing)
- ✅ Production-ready architecture
- ✅ Optimized & clean code

## 🚀 30-Second Setup

1. **Open in Android Studio**
   ```
   File → Open → Select: /path/to/dollar-block/
   ```

2. **Wait for Gradle Sync**
   - Bottom right: "Gradle sync in progress..."
   - Wait 1-3 minutes (first time only)

3. **Click Run**
   - Green ▶️ button at top
   - Select device or create emulator
   - App installs and launches!

## �️ Development Workflow (Claude Code + Android Studio)

**Recommended Setup:**
| Tool | Purpose |
|------|---------|
| **Android Studio** | Open `a:\DollarBlock` - for building, running, debugging, emulator |
| **Claude Code** | Code edits, Git operations, file management |

**When to Sync Gradle:**
| Change Type | What to Do |
|-------------|------------|
| `.kt` / `.java` files | **Nothing!** Just rebuild (Ctrl+F9) or Run ▶️ |
| XML layouts / strings | **Nothing!** Just rebuild |
| `build.gradle.kts` edited | Click "Sync Now" banner (~30 sec) |
| New dependencies added | Click "Sync Now" banner |
| Gradle version changed | Full restart recommended |

**Pro Tip:** Enable auto-sync in Android Studio:
- `File → Settings → Build → Compiler`
- ✅ Check "Sync project with Gradle files when project changes"

This way most code changes are picked up automatically - just hit Run ▶️!

## �📱 Testing Checklist

- [ ] Launch app → See onboarding
- [ ] Click "Link Card & Start Blocking →"
- [ ] Toggle Instagram/TikTok ON
- [ ] Click "DONE"
- [ ] Go to Settings → Accessibility → Enable "Dollar Block"
- [ ] Try to open Instagram
- [ ] See "We want you to pay.\nGive in."
- [ ] Click "Pay $1.00 to Unblock"
- [ ] See "Weak.\nPREDICTABLE OUTCOME."
- [ ] Watch progress bar count down
- [ ] App unblocks automatically

## 📂 Files You'll Want to Check

```
dollar-block/
├── BUILD_INSTRUCTIONS.md    ← Detailed Android Studio guide
├── TESTING_GUIDE.md          ← All test scenarios
├── PROJECT_SUMMARY.md        ← Technical documentation
├── QUICK_START.md            ← This file
└── app/src/main/java/com/dollarblock/app/
    ├── OnboardingActivity.kt ← "Discipline is cheap"
    ├── MainActivity.kt        ← "Choose your poison"
    └── UnblockActivity.kt     ← Block & Shame screens
```

## 🎨 Code is Clean & Optimized

✅ **No payment SDK** (Stripe removed for simplicity)
✅ **Minification enabled** (smaller APK)
✅ **ProGuard configured** (optimized builds)
✅ **Skip onboarding** (after first launch)
✅ **Strings extracted** (ready for translation)
✅ **Dimensions extracted** (easy theming)
✅ **No unused imports**
✅ **MVVM architecture**
✅ **Kotlin best practices**

## 🔥 The Flow

```
Launch
  ↓
Onboarding
  ↓
App Selection (toggle switches)
  ↓
Enable Accessibility (in Settings)
  ↓
Try blocked app
  ↓
Block Screen ("We want you to pay")
  ↓
Click "Pay" OR "I'll Stay Focused"
  ↓
Shame Screen (if paid)
  ↓
Progress bar → Auto close
```

## 💡 Pro Tips

1. **First time?** Read `BUILD_INSTRUCTIONS.md`
2. **Want to test?** Read `TESTING_GUIDE.md`
3. **Want details?** Read `PROJECT_SUMMARY.md`
4. **Just want to run it?** Follow the 30-second setup above!

## 🐛 Common Issues

**"Gradle sync failed"**
→ File → Invalidate Caches → Restart

**"Accessibility not working"**
→ Settings → Accessibility → Dollar Block → Toggle OFF then ON

**"Can't find app in launcher"**
→ Check you're running the right build variant (debug)

## 📊 Build Stats

- **Language**: 100% Kotlin
- **Lines of Code**: ~1,200
- **Screens**: 4 (Onboarding, Selection, Block, Shame)
- **APK Size**: ~5MB (estimated with minification)
- **Min Android**: 7.0 (API 24)
- **Target Android**: 14 (API 34)

## ✅ Ready to Ship?

Current version: **Mockup/Testing Build**
- ❌ No real payment processing
- ❌ No backend API
- ❌ No analytics
- ✅ Full UI/UX flow
- ✅ Real app blocking
- ✅ Production architecture

To make it production-ready:
1. Add Stripe SDK back
2. Integrate payment processing
3. Build backend API
4. Add analytics
5. Submit to Play Store

## 🎉 That's It!

Open Android Studio, click Run, and test the app!

Questions? Check the other documentation files.

---

**Branch**: `claude/dollar-block-mockups-Wp6zC`
**Status**: ✅ Ready to test
**Next**: Open in Android Studio and click Run!
