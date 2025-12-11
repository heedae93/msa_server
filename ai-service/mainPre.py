from fastapi import FastAPI, UploadFile, File
from ultralytics import YOLO
from PIL import Image
import io
import sys
print("내 집 주소:", sys.prefix)
app = FastAPI()

# 1. AI 모델 로드
# yolov8n-cls.pt: 가장 가볍고 빠른 분류 모델 (처음 실행 시 자동 다운로드됨)
print("AI 모델을 불러오는 중입니다...")
model = YOLO('yolov8n-cls.pt')
print("모델 로드 완료!")

@app.get("/")
def home():
    return {"message": "멍멍! AI 서버가 정상 작동 중입니다. 🐶"}

@app.post("/analyze")
async def analyze_dog(file: UploadFile = File(...)):
    # 2. 클라이언트(앱)에서 보낸 이미지 읽기
    image_data = await file.read()
    image = Image.open(io.BytesIO(image_data))

    # 3. AI에게 이미지 보여주고 분석 시키기
    results = model(image)

    # 4. 분석 결과 중 가장 확률 높은 것 뽑기
    # (probs.top1은 1등 예측값의 인덱스, top1conf는 그 확률)
    top1_index = results[0].probs.top1
    label = results[0].names[top1_index]
    probability = float(results[0].probs.top1conf)

    # 5. 결과 반환 (JSON)
    return {
        "result": label,           # 예: "golden_retriever" (아직 감정 모델 아님)
        "probability": probability, # 예: 0.92 (92% 확신)
        "message": f"이 강아지는 {probability*100:.1f}% 확률로 {label} 입니다!"
    }