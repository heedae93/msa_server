from fastapi import FastAPI, UploadFile, File
from ultralytics import YOLO
from PIL import Image
import io
import sys

app = FastAPI()

# ==========================================
# ★ 중요: 학습이 끝나면 생성될 모델 경로 지정
# ==========================================
# 학습이 아직 안 끝났을 때는 이 줄에서 에러가 날 수 있으니,
# 학습 완료 후 best.pt 파일이 생긴 것을 확인하고 서버를 켜세요.
model_path = './dog_emotion_project/dog_emotion_model/weights/best.pt'

print(f"AI 모델을 불러오는 중입니다... 경로: {model_path}")
try:
    model = YOLO(model_path)
    print("성공! 나만의 강아지 감정 모델이 로드되었습니다. 🐶")
except Exception as e:
    print("⚠ 아직 학습 파일이 없거나 경로가 틀렸습니다.")
    print("학습이 완료될 때까지 기다려주세요.")
    # 임시로 기본 모델 로드 (서버 테스트용)
    model = YOLO('yolov8n-cls.pt')

@app.get("/")
def home():
    return {"message": "멍멍! 강아지 감정 분석 AI 서버입니다. 🐶"}

@app.post("/analyze")
async def analyze_dog(file: UploadFile = File(...)):
    # 1. 이미지 읽기
    image_data = await file.read()
    image = Image.open(io.BytesIO(image_data))

    # 2. AI 예측
    results = model(image)

    # 3. 결과 분석
    # names = {0: 'angry', 1: 'happy', ...}
    names = results[0].names

    # 가장 확률 높은 감정(Top 1) 뽑기
    top1_index = results[0].probs.top1
    top1_label = names[top1_index]
    top1_conf = float(results[0].probs.top1conf) # 확률 (0.0 ~ 1.0)

    # 4. JSON 응답 반환
    return {
        "status": "success",
        "result": {
            "emotion": top1_label,      # 예: happy
            "confidence": f"{top1_conf * 100:.1f}%"  # 예: 98.5%
        },
        "message": f"이 강아지는 {top1_conf*100:.1f}% 확률로 [{top1_label}] 상태입니다!"
    }