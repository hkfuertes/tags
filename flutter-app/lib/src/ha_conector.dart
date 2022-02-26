import 'dart:convert';

import 'package:http/http.dart' as http;

class HAConnector {
  static const endpoint = "/api/events/tag_scanned";
  //static const body = {"tag_id": "<tag_id>"};
  String ip, port, pat, name;

  HAConnector(
      {required this.ip,
      required this.port,
      required this.pat,
      required this.name});

  Uri _createUri() {
    return Uri.parse("http://" + ip + ":" + port + endpoint);
  }

  Map<String, String> _createBody(String tagId) {
    return {"tag_id": tagId};
  }

  Future tagRead(String tagId) async {
    return http.post(_createUri(),
        body: json.encode(_createBody(tagId)),
        headers: {
          "Authorization": "Bearer " + pat,
          "Content-Type": "application/json"
        }).then((http.Response response) {
      final int statusCode = response.statusCode;

      if (statusCode < 200 || statusCode > 400) {
        throw new Exception("Error while fetching data");
      }
      return;
    });
  }
}
