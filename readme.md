## NFC HA Tags
Simple proxy app to trigger Home Assistant tag by reusing rfid cards (i.e. Metro card).

### Usage
If the app/service is enabled, any NFC card/tag read without any android-compatible payload, will be read, and the app will send a signal with the card ID to Home Assistant, via Intent or via API.

### WIP
**TODO**:
- Test read card id and payload. (Done in Flutter)
  - If payload is not detected we act based upon CARDID
- Test read wifi network and list available networks.
  - **Optional**: If set, we can do any of the actions based on the location (via wifi availability/connection)
- Send intent to app: 
  - Intent: `android.intent.action.VIEW`
  - Data: `https://www.home-assistant.io/tags/<tag_id>`
  - Package: `io.homeassistant.companion.android[.minimal]`
- Do API call: (Done in Flutter)
  - API endpoint: `/api/events/tag_scanned`
  - Data: `{"tag_id":"<tag_id>"}`
  - Auth: `Bearer <PAT>`
  
### Tasker Profile
I already have a [tasker profile](tasker-profile.xml) that does this.

### Flutter App
Reads the ID of a card and sends it to home assitant via API (PAT needed). Next Steps, auto send, react on app closed (bradcast receiver), intent mode...