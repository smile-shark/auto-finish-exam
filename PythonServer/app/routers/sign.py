from fastapi import APIRouter
from pydantic import BaseModel
import execjs
import json

class SignData(BaseModel):
    json:str

router=APIRouter(prefix='/sign',tags=['签名模块'])

def test_execjs(data:str):

    val=json.loads(data)
    with open('resources/app.js',encoding='utf-8') as f:
       jscode=f.read()
    ctll=execjs.compile(jscode)

    # {'kpId': '383a384fabdb4b6aa957689e874006eb'}
    sign=ctll.call('generateHmacSHA256Sign',val['json'])

    return sign

@router.post('/decode')
async def decode_sign(data:SignData) -> str:
    print(data)
    return test_execjs(data.model_dump_json())

if __name__ == '__main__':
    print(test_execjs('{"json":{"kpId": "383a384fabdb4b6aa957689e874006eb"}}'))