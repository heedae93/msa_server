from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware # 보안 설정
from ultralytics import YOLO
from PIL import Image
import io
import logging # 로깅 도구

# 1. 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# 2. CORS 보안 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # 실제 배포시는 ip나 도메인 특정해야 함
    allow_methods=["*"],
    allow_headers=["*"],
)

# 모델 로드 (생략 - 기존과 동일)
model = YOLO('yolov8n-cls.pt')

@app.post("/analyze")
async def analyze_dog(file: UploadFile = File(...)):
    # 3. 파일 검증 (이미지가 아니면 거절)
    if file.content_type not in ["image/jpeg", "image/png"]:
        raise HTTPException(status_code=400, detail="JPG나 PNG 이미지만 보내주세요! 🚫")

    try:
        # 이미지 읽기
        image_data = await file.read()
        image = Image.open(io.BytesIO(image_data))

        # 4. AI 예측 (에러 발생 가능 구간)
        results = model(image)

        # 결과 추출
        top1_index = results[0].probs.top1
        top1_label = results[0].names[top1_index]
        top1_conf = float(results[0].probs.top1conf)

        # 로그 남기기
        logger.info(f"분석 완료: {top1_label} ({top1_conf:.2f})")

        return {
            "status": "success",
            "result": {
                "emotion": top1_label,
                "confidence": f"{top1_conf * 100:.2f}"
            }
        }

    except Exception as e:
        # 5. 예상치 못한 에러 처리
        logger.error(f"에러 발생: {str(e)}")
        return {
            "status": "error",
            "message": "서버 내부에서 문제가 생겼습니다. 😢"
        }