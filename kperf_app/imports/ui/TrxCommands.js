import React from 'react'
import { Redirect } from 'react-router-dom'
import { Meteor } from 'meteor/meteor'
import { Container, Row, Col, InputGroup, FormControl, Card, ButtonGroup, Button } from 'react-bootstrap'

export default class TrxCommands extends React.Component {

  render() {
    return (

        <label htmlFor="basic-url">Your vanity URL</label>
        <InputGroup className="mb-3">
        <InputGroup.Prepend>
          <InputGroup.Text id="basic-addon3">
            Current State
          </InputGroup.Text>
        </InputGroup.Prepend>
        <FormControl id="trx-gen-state" aria-describedby="basic-addon3" disabled />
        </InputGroup>
        <ButtonGroup>
          <Button variant="success">Start</Button>
          <Button variant="warning">Stop</Button>
          <Button variant="danger">Exit</Button>
        </ButtonGroup>

    )
  }
}
