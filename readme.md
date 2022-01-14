## NFC HA Tags
Simple proxy app to trigger Home Assistant tag by reusing rfid cards (i.e. Metro card).

### Usage
If the app/service is enabled, any NFC card/tag read without any android-compatible payload, will be read, and the app will send a signal with the card ID to Home Assistant, via Intent or via API.

### WIP
**TODO**:
- Test read card id and payload.
  - If payload is not detected we act based upon CARDID
- Test read wifi network and list available networks.
  - **Optional**: If set, we can do any of the actions based on the location (via wifi availability/connection)
- Send intent to app: 
  - Intent: `android.intent.action.VIEW`
  - Data: `https://www.home-assistant.io/tags/<tag_id>`
  - Package: `io.homeassistant.companion.android[.minimal]`
- Do API call:
  - API endpoint: `/api/events/tag_scanned`
  - Data: `{"tag_id":"<tag_id>"}`
  - Auth: `Bearer <PAT>`
  
### Tasker Profile
I already have a tasker profile that does this:
```xml
<TaskerData sr="" dvi="1" tv="5.15.8-beta">
 <Profile sr="prof6" ve="2">
  <cdate>1642002885505</cdate>
  <edate>1642064015758</edate>
  <flags>8</flags>
  <id>6</id>
  <mid0>5</mid0>
  <nme>TAG-HASS</nme>
  <Event sr="con0" ve="2">
   <code>2076</code>
   <pri>0</pri>
   <Str sr="arg0" ve="3"/>
   <Str sr="arg1" ve="3"/>
  </Event>
  <State sr="con1" ve="2">
   <code>170</code>
   <Str sr="arg0" ve="3">[WIFI_NAME]></Str>
   <Str sr="arg1" ve="3"/>
   <Str sr="arg2" ve="3"/>
   <Int sr="arg3" val="0"/>
   <Int sr="arg4" val="0"/>
   <Int sr="arg5" val="0"/>
  </State>
 </Profile>
 <Task sr="task5">
  <cdate>1642001342202</cdate>
  <edate>1642064008383</edate>
  <id>5</id>
  <nme>HASSTAGMIN</nme>
  <pri>6</pri>
  <Action sr="act0" ve="7">
   <code>548</code>
   <Str sr="arg0" ve="3">%evtprm(1)</Str>
   <Int sr="arg1" val="0"/>
   <Str sr="arg10" ve="3"/>
   <Int sr="arg11" val="1"/>
   <Int sr="arg12" val="0"/>
   <Str sr="arg13" ve="3"/>
   <Int sr="arg14" val="0"/>
   <Int sr="arg2" val="0"/>
   <Str sr="arg3" ve="3"/>
   <Str sr="arg4" ve="3"/>
   <Str sr="arg5" ve="3"/>
   <Str sr="arg6" ve="3"/>
   <Str sr="arg7" ve="3"/>
   <Str sr="arg8" ve="3"/>
   <Int sr="arg9" val="1"/>
  </Action>
  <Action sr="act1" ve="7">
   <code>877</code>
   <Str sr="arg0" ve="3">android.intent.action.VIEW</Str>
   <Int sr="arg1" val="0"/>
   <Str sr="arg2" ve="3"/>
   <Str sr="arg3" ve="3">https://www.home-assistant.io/tag/%evtprm(1)</Str>
   <Str sr="arg4" ve="3"/>
   <Str sr="arg5" ve="3"/>
   <Str sr="arg6" ve="3"/>
   <Str sr="arg7" ve="3">io.homeassistant.companion.android.minimal</Str>
   <Str sr="arg8" ve="3"/>
   <Int sr="arg9" val="1"/>
  </Action>
 </Task>
</TaskerData>
```