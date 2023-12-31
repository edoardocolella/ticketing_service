import { useState } from "react";
import { Button, Col, Container, Form, Row } from "react-bootstrap";
import { API } from "../API";
import { ProfileModal } from "./ProfileModal";
import { errorHandler } from "./ErrorHandler";
import { successToast } from "./SuccessToast";
import dayjs from "dayjs";

export function GetProfileByEmailAddress() {

    const [profile, setProfile] = useState([]);
    const [show, setShow] = useState(false);
    const [email, setEmail] = useState("");

    let getProfile = async () => {

        if (!emailIsValid(email)) {
            return errorHandler("Email is not valid")
        }

        await API.getProfileByEmailAddress(email)
            .then(profile => { setProfile(profile); setShow(true) })
            .catch(error => errorHandler(error));
    }

    return <>
        <Container>
            <Row >
            <Col xl={3}/>
                <Col xl={6}>
                    <Row><p style={{ textAlign: 'center' }}>FIND A PROFILE BY EMAIL</p></Row>
                    <Row>
                    <Form>
                            <Form.Group className="mb-3" controlId="email">
                                <Form.Label>Email</Form.Label>
                                <Form.Control value={email} placeholder="Enter email" onChange={e => setEmail(e.target.value)} />
                        </Form.Group>
                        </Form>
                    </Row>
                    <Row><Button onClick={getProfile}>SEND REQUEST</Button></Row>
                    <ProfileModal show={show} profile={profile} handleClose={() => setShow(false)} />
                </Col>
            <Col xl={3}/>
            </Row>
            
        </Container>
    </>
}

export function CreateProfile() {

    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [birthDate, setBirtdate] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");

    let createProfile = async () => {

        if (!emailIsValid(email)) {
            return errorHandler("Email is not valid")
        }
        if (!parameterIsValid(name)) {
            return errorHandler("Name is empty")
        }
        if (!parameterIsValid(surname)) {
            return errorHandler("Surname is empty")
        }
        if (!parameterIsValid(birthDate)) {
            return errorHandler("Birthdate is empty")
        }
        if (!phoneIsValid(phoneNumber)) {
            return errorHandler("Phone number is not valid")
        }

        let profile = {
            email: email,
            name: name,
            surname: surname,
            birthDate: dayjs(birthDate).format("YYYY-MM-DD").toString(),
            phoneNumber: phoneNumber
        }
        await API.createProfile(profile)
            .then(_ => { successToast("Profile created"); resetStates()} )
            .catch(error => errorHandler(error));
    }

    const resetStates = () => {
        setEmail("");
        setName("");
        setSurname("");
        setBirtdate("");
        setPhoneNumber("");
    }

    return <>
        <Container>
            <Row>
            <Col xl={3}/>
                <Col>
                    <Row><p style={{ textAlign: 'center' }}>CREATE A PROFILE</p></Row>
                    <Row>
                        <Form>
                            <Form.Group className="mb-3" controlId="email">
                                <Form.Label>Email</Form.Label>
                                <Form.Control value={email} placeholder="Enter email" onChange={e => setEmail(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="name">
                                <Form.Label>Name</Form.Label>
                                <Form.Control value={name} placeholder="Enter name" onChange={e => setName(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="surname">
                                <Form.Label>Surname</Form.Label>
                                <Form.Control value={surname} placeholder="Enter surname" onChange={e => setSurname(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="birthDate">
                                <Form.Label>birthDate</Form.Label>
                                <Form.Control value={birthDate} type="date" placeholder="Enter birthDate" onChange={e => setBirtdate(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="phoneNumber">
                                <Form.Label>Phone Number</Form.Label>
                                <Form.Control value={phoneNumber} placeholder="Enter PhoneNumber" onChange={e => setPhoneNumber(e.target.value)} />
                            </Form.Group>                           
                        </Form>
                    </Row>
                    <Row><Button onClick={createProfile}>SEND REQUEST</Button></Row>
                </Col>
            <Col xl={3}/>
            </Row>
        </Container>
    </>
}

export function UpdateProfile() {

    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [birthDate, setBirtdate] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");

    let updateProfile = async () => {
        if (!emailIsValid(email)) {
            return errorHandler("Email is not valid")
        }
        if (!parameterIsValid(name)) {
            return errorHandler("Name is empty")
        }
        if (!parameterIsValid(surname)) {
            return errorHandler("Surname is empty")
        }
        if (!parameterIsValid(birthDate)) {
            return errorHandler("Birthdate is empty")
        }
        if (!phoneIsValid(phoneNumber)) {
            return errorHandler("Phone number is not valid")
        }

        let profile = {
            name: name,
            surname: surname,
            birthDate: dayjs(birthDate).format("YYYY-MM-DD").toString(),
            phoneNumber: phoneNumber
        }
        await API.updateProfile(email,profile)
            .then(_ => { successToast("Profile updated");  resetStates()})
            .catch(error => { errorHandler(error);});
    }

    const resetStates = () => {
        setEmail("");
        setName("");
        setSurname("");
        setBirtdate("");
        setPhoneNumber("");
    }
    
    return <>
        <Container>
            <Row>
            <Col xl={3}/>

                <Col>
                    <Row><p style={{ textAlign: 'center' }}>EDIT A PROFILE</p></Row>
                    <Form>
                            <Form.Group className="mb-3" controlId="email">
                                <Form.Label>Email</Form.Label>
                            <Form.Control value={email} placeholder="Enter email" onChange={e => setEmail(e.target.value)}/>
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="name">
                                <Form.Label>Name</Form.Label>
                                <Form.Control value={name} placeholder="Enter name" onChange={e => setName(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="surname">
                                <Form.Label>Surname</Form.Label>
                                <Form.Control value={surname} placeholder="Enter surname" onChange={e => setSurname(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="birthDate">
                                <Form.Label>birthDate</Form.Label>
                                <Form.Control value={birthDate} type="date" placeholder="Enter birthDate" onChange={e => setBirtdate(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="phoneNumber">
                                <Form.Label>Phone Number</Form.Label>
                                <Form.Control value={phoneNumber}  placeholder="Enter PhoneNumber" onChange={e => setPhoneNumber(e.target.value)} />
                            </Form.Group>                           
                        </Form>
                    <Row><Button onClick={updateProfile}>SEND REQUEST</Button></Row>
                </Col>
            <Col xl={3}/>

            </Row>
            
        </Container>
    </>
}

function emailIsValid(email) {
    const regExpMail = new RegExp("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$");
    return regExpMail.test(email)
}

function phoneIsValid(phone) {
    console.error("fafwafawf")
    const regExpPhone = new RegExp("[-+0-9 ]{7,15}");
    console.log("dadw" + phone)
    return regExpPhone.test(phone)
}

function parameterIsValid(parameter) {
return !(parameter === "" || parameter == null || parameter === undefined);
}